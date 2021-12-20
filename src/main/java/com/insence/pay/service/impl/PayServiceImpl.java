package com.insence.pay.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.insence.pay.dao.PayInfoMapper;
import com.insence.pay.pojo.PayInfo;
import com.insence.pay.service.IPayService;
import com.insence.pay.util.PayUtil;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PayServiceImpl implements IPayService {
    private final static String QUEUE_PAY_NOTIFY = "payNotify";

    @Autowired
    PayInfoMapper payInfoMapper;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Override
    public Map create(String orderId, int payType, BigDecimal money) {
        //1写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                payType,
                OrderStatusEnum.NOTPAY.name(),
                money);
        payInfoMapper.insertSelective(payInfo);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> remoteMap = new HashMap<String, Object>();
        remoteMap.put("money", money);
        remoteMap.put("pay_type", payType);
        remoteMap.put("order_no", orderId);
        remoteMap.put("subject", "商品名称");
        remoteMap.put("app_id", PayUtil.APP_ID);
        remoteMap.put("extra", "");
        remoteMap = PayUtil.payOrder(remoteMap);
        String post = HttpUtil.post("https://gateway.xddpay.com?format=json", remoteMap);

        //判断是否正确返回
        resultMap = (Map)JSON.parse(post);
        log.debug("发起支付 response={}", resultMap.get("msg"));
        //没有二维码有问题
        if(resultMap.get("qr")=="") {
            log.debug("创建出问题");
        }
        return resultMap;

//            map.keySet().stream().forEach(k -> {
//                if (k == "qr") {
//                    log.debug("url二维码链接是: "+map.get("qr"));
//                }
//            });

//        log.info("发起支付 response={}", resultMap);
    }



    /**
     * 异步通知处理
     *
     * @param notifyData
     */
    @Override
    public String asyncNotify(Map notifyData) {
        System.out.println(notifyData);
        //1.签名校验
        String BackSign = (String) notifyData.get("sign");
        String realSign = PayUtil.getBackSign(notifyData).toUpperCase();
        if(!BackSign.equals(realSign)) {
            log.debug("签名错误");
        } else {
            log.debug("签名正确");
        }

        //2.金额校验（从数据库查订单）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong((String) notifyData.get("order_no")));
        if (payInfo == null) {
            //告警
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }
        //支付状态是未支付 核对金额
        if(!payInfo.getPlatformStatus().equals("已支付")) {
            //Double类型比较大小，精度。1.00  1.0
            if (payInfo.getPayAmount().compareTo(new BigDecimal(String.valueOf(notifyData.get("realmoney")))) != 0) {
                //告警
                throw new RuntimeException("异步通知中的金额和数据库里的不一致，orderNo=" + notifyData.get("order_no"));
            }
        }

        //3.修改订单支付状态
        payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
        payInfo.setPlatformNumber((String) notifyData.get("xddpay_order"));
        //默认由sql处理
        payInfo.setUpdateTime(null);
        payInfoMapper.updateByPrimaryKeySelective(payInfo);

        //发送mq  把payInfo传过去
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY, new Gson().toJson(payInfo));

        //4.通知success 告知api
        return "success";
    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
