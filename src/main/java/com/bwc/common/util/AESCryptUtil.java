package com.bwc.common.util;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AESCryptUtil.java
 * @Description : 클래스 설명을 기술합니다.
 * @author choi young kwang
 * @since 2018. 8. 10.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018. 8. 10.     choi young kwang         최초 생성
 * </pre>
 */

public class AESCryptUtil {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private String merchantKey;
	private Cipher cipher;

	public static byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00};

	public AESCryptUtil(String merchantKey) {
		this.merchantKey = merchantKey;

		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] privateKey = new byte[32];
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			privateKey = initialPrivateKey();

			SecretKeySpec keySpec = new SecretKeySpec(privateKey, "AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		} catch (Exception e) {
			logger.error("▷▷AESCryptUtil Exception : merchantKey = {} ", merchantKey);
			logger.error("error :  {} ", e);
		}
	}

	/**
	 * 평문을 암호화한다.
	 *
	 * @param plainText 암호화에 사용될 세션키
	 * @return 암호화된 결제관련 전문
	 * @throws Exception
	 */
	public String encrypt(String plainText) throws Exception {
		byte[] privateKey = new byte[32];
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		privateKey = initialPrivateKey();

		SecretKeySpec keySpec = new SecretKeySpec(privateKey, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] doFinal = cipher.doFinal(plainText.getBytes());
		return new String(Base64.encodeBase64(doFinal));
	}

	/**
	 * 암호문을 평문으로 해독
	 *
	 * @param encryptedValue 암호화 전문
	 * @return 복호화된 결제관련 전문
	 * @throws Exception
	 */
	public String decrypt(String encryptedValue) throws Exception {
		byte[] plainText = Base64.decodeBase64(encryptedValue.getBytes());
		try {
			byte[] privateKey = new byte[32];
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			privateKey = initialPrivateKey();

			SecretKeySpec keySpec = new SecretKeySpec(privateKey, "AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			return new String(cipher.doFinal(plainText));
		} catch (Exception e) {
			logger.error("▷▷decrypt Exception : encryptedValue = {} ", encryptedValue);
			return "";
		}

	}

	private byte[] initialPrivateKey() {
		String needKey = merchantKey.substring(0, 32);

		byte[] keyBytes = new byte[32];
		byte[] b = needKey.getBytes();

		int len = b.length;

		if (len > keyBytes.length) {
			len = keyBytes.length;
		}

		System.arraycopy(b, 0, keyBytes, 0, len);

		return keyBytes;
	}

	// public static void main(String[] args) throws Exception {
	// 	String key = "I+MbrTRDO53NHU+KLXm3NzjBMYjwl7r2";
	//
	// 	AESCryptUtil aecUtil = new AESCryptUtil(key);
	// 	String encryptText = aecUtil.encrypt("1520669");
	// 	System.out.println("AES 암호화 ::" + encryptText);
	// 	System.out.println("AES 복호화 ::" + aecUtil.decrypt(encryptText));
	//
	// 	String url = "http://www.test.aaa";
	// 	System.out.println(Base64.encodeBase64String(url.getBytes()));
	//
	// }
}
