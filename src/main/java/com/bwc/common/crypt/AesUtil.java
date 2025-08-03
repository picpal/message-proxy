package com.bwc.common.crypt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import com.bwc.common.util.AESCryptUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AesUtil {

	public static String doEncrypt(String key, String plainText) {

		plainText = StringUtils.defaultString(plainText);
		String retStr = "";

		AESCryptUtil aecUtil = new AESCryptUtil(key);
		try {
			retStr = aecUtil.encrypt(plainText);
		} catch (Exception e) {
			log.info("AesUtil Exception");
			// e.printStackTrace();
		}
		return retStr;
	}

	public static String doDecrypt(String key, String plainText) {

		plainText = StringUtils.defaultString(plainText);
		String retStr = "";

		AESCryptUtil aecUtil = new AESCryptUtil(key);
		try {
			retStr = aecUtil.decrypt(plainText);
		} catch (Exception e) {
			log.info("doDecrypt Exception");
			// e.printStackTrace();
		}
		return retStr;
	}

	public static byte[] generateKey() throws Exception {

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey key = keyGenerator.generateKey();

		byte[] keybytes = key.getEncoded();

		return keybytes;
	}
}
