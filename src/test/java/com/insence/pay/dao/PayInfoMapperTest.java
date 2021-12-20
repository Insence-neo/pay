package com.insence.pay.dao;

import com.insence.pay.PayApplicationTests;
import com.insence.pay.pojo.PayInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class PayInfoMapperTest extends PayApplicationTests {

    @Autowired
    PayInfoMapper payInfoMapper;
    @Test
    public void selectByPrimaryKey() {
        PayInfo payInfo = payInfoMapper.selectByPrimaryKey(1);
        System.out.println(payInfo.toString());
    }
}