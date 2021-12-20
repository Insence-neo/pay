package com.insence.pay.service;

import com.insence.pay.pojo.PayInfo;
import com.lly835.bestpay.model.PayResponse;

import java.math.BigDecimal;
import java.util.Map;

public interface IPayService {

    /**
     * 创建/发起支付
     */
    Map create(String orderId, int payType, BigDecimal money);

    String asyncNotify(Map notifyData);

    PayInfo queryByOrderId(String orderId);
}
