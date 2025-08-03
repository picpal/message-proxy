package com.bwc.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Jasypt를 이용하여 properties value 암호화 값 추출에 사용
 *
 * ############### 주의사항 ################
 * 1) 해당 파일은 Commit 금지!!!
 * 2) 암호화한 다음 encTexts는 꼭 제거!!!
 * ########################################
 *
 * */
@Slf4j
class JasyptConigAESTest {

	/**
	 * jasypt properties encrypt를 위한
	 * 채번 및 properties value 암호화
	 *
	 * 채번된 key는 2가지로 나눠서 관리
	 * (채번된 키를 가지고 복호화 하기 때문에 출력된 로그 잘 확인해야 함 )
	 * => 로그 출력 확인 : pre , post
	 *
	 * 채번된 key로 암호화
	 * => 로그 출력 확인 : ex) user => qsoafuwel@$!)@llso=
	 *
	 * */
	@Test
	void stringEncryptor() {
		String[] encTexts = {
			"messageGateApiKey"
		};

		// 암호화 키 신규 생성
		// String encKey = getKey();
		// log.info("####### ENCKEY =>> jasypt.opt.pre : {} , jasypt.opt.post : {}"
		// 	, encKey.substring(0, 10)
		// 	, encKey.substring(10));

		// 기존 암호화 키 사용
		String encKey = "MjAyNDAyMDYwODQ1MzE=";

		for (String encText : encTexts) {
			log.info("####### {} => {}", encText, jasyptEncoding(encKey, encText));
		}
	}

	private String jasyptEncoding(String encKey, String value) {
		if (StringUtils.isEmpty(encKey)) {
			log.error("Not Found ENC_KEY");
			return "";
		}

		StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
		pbeEnc.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
		pbeEnc.setPassword(encKey);
		pbeEnc.setIvGenerator(new RandomIvGenerator());

		return pbeEnc.encrypt(value);
	}

	private String getKey() {
		LocalDateTime today = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String formatDate = today.format(formatter);
		Base64.Encoder encoder = Base64.getEncoder();
		String encodeStr = encoder.encodeToString(formatDate.getBytes());

		// log.info("####### KEY  =>> {} ", encodeStr);
		return encodeStr;
	}

}