package com.insence.pay.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Getter;


@Getter
public enum PayPlatformEnum {

	//43-支付宝,44-微信
	ALIPAY(43),

	WX(44),
	;

	Integer code;

	PayPlatformEnum(Integer code) {
		this.code = code;
	}
}
