package com.insence.pay.service.impl;

import com.insence.pay.PayApplicationTests;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static com.alibaba.druid.util.Utils.md5;
import static jdk.nashorn.internal.objects.NativeString.toLowerCase;
import static org.junit.Assert.*;

public class PayServiceImplTest extends PayApplicationTests {

    @Autowired
    private PayServiceImpl payService;

    //mq
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void create() {
        payService.create("1234",43, BigDecimal.valueOf(0.04));
    }

    @Test
    public void sendMQ() {
        amqpTemplate.convertAndSend("payNotify","123");
    }
}