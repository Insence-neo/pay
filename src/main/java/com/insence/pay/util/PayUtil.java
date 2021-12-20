package com.insence.pay.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.insence.pay.config.*;

public class PayUtil {

	private static Logger log = LogManager.getLogger();

	public static String BASE_URL = "http://api.6688pay.com";

	public static String APP_ID = "12666";	//见后台
	public static String APP_SECRET = "7966f28f7dea4a429c967d8b244c5b9d";	//见后台

	public static Map<String, Object> payOrder(Map<String, Object> remoteMap) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.putAll(remoteMap);
		paramMap.put("sign", getSign(remoteMap));
		return paramMap;
	}

	public static String getSign(Map<String, Object> remoteMap) {
		String key = "";
		
		key += "order_no=" + remoteMap.get("order_no");
		key += "&subject=" + remoteMap.get("subject");
		key += "&pay_type=" + remoteMap.get("pay_type");
		key += "&money=" + remoteMap.get("money");
		key += "&app_id=" + remoteMap.get("app_id");
		key += "&extra=" + remoteMap.get("extra");
		key += "&" + APP_SECRET;
		
		return MD5Util.encryption(key);
	}

	public static String getBackSign(Map<String, Object> remoteMap) {
		String key = "";

		key += "order_no=" + remoteMap.get("order_no");
		key += "&subject=" + remoteMap.get("subject");
		key += "&pay_type=" + remoteMap.get("pay_type");
		key += "&money=" + remoteMap.get("money");
		key += "&realmoney=" + remoteMap.get("realmoney");
		key += "&result=" + remoteMap.get("result");
		key += "&xddpay_order=" + remoteMap.get("xddpay_order");
		key += "&app_id=" + remoteMap.get("app_id");
		key += "&extra=" + remoteMap.get("extra");
		key += "&" + APP_SECRET;

		return MD5Util.encryption(key);
	}

//	public static boolean checkSign(PayConfig payConfig) {
//		String key = "";
//		key += "order_no=" + payConfig.getOrder_no();
//		key += "&subject=" + payConfig.getSubject();
//		key += "&pay_type=" + payConfig.getPay_type();
//		key += "&money=" + payConfig.getMoney();
//		key += "&realmoney=" + payConfig.getRealmoney();
//		key += "&result=" + payConfig.getResult();
//		key += "&xddpay_order=" + payConfig.getXddpay_order();
//		key += "&app_id=" + payConfig.getApp_id();
//		key += "&extra=" + payConfig.getExtra();
//		key += "&" + APP_SECRET;
//		log.debug("支付回来的Key：" + payConfig.getSign());
//		log.debug("我们自己拼接的Key：" + MD5Util.encryption(key));
//		return payConfig.getSign().equals(MD5Util.encryption(key));
//	}

	public static String getOrderIdByUUId() {
		int machineId = 1;// 最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		// 0 代表前面补充0;d 代表参数为正数型
		return machineId + String.format("%01d", hashCodeV);
	}

}
