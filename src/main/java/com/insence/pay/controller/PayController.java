package com.insence.pay.controller;


import com.alibaba.fastjson.JSONObject;
import com.insence.pay.config.WxAccountConfig;
import com.insence.pay.pojo.PayInfo;
import com.insence.pay.service.impl.PayServiceImpl;
import com.insence.pay.util.PayUtil;
import com.lly835.bestpay.config.WxPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("pay")
@Slf4j
public class PayController {

    @Autowired
    PayServiceImpl payService;

    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("create")
    public String create(Model model,
                         @RequestParam("orderId") String orderId,
                         @RequestParam("payType") int payType,
                         @RequestParam("amount") BigDecimal amount) {
        Map resultMap = payService.create(orderId,payType,amount);
        System.out.println(resultMap);
        model.addAttribute("qr",resultMap.get("qr"));
        model.addAttribute("orderId",orderId);
        model.addAttribute("returnUrl",wxPayConfig.getReturnUrl());
        return "createForWx";      //视图名
    }

    @PostMapping(value="/notify")
    @ResponseBody
    public String asyncNotify(@RequestParam Map<String,Object> notifyData) {
        payService.asyncNotify(notifyData);
//        // 将获取的json数据封装一层，然后在给返回
//        JSONObject result = new JSONObject();
//        result.put("code", "200");
//        result.put("content", "OK");
        return "success";
    }

    @GetMapping("/queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam("orderId") String orderId) {
        log.debug("查询支付记录...");
        return payService.queryByOrderId(orderId);
    }

    @GetMapping("/hello")
        //mvc接收前端请求数据
        public String pay(Model model,
                          @RequestParam("orderId") String orderId,
                          @RequestParam("payType") int payType,
                          @RequestParam("amount") BigDecimal amount) {
            return "redirect:/employee/lists";
        }
}

