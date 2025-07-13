package com.bwc.common.crypt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bwc.message.v1.gate.dao.MaMessageDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KeyGenerator {

	@Value("${app.enckey}")
	private String enckey;

	private final PasswordEncoder passwordEncoder;

	private final MaMessageDAO maMessageDAO;

	@Autowired
	public KeyGenerator(PasswordEncoder passwordEncoder, MaMessageDAO maMessageDAO) {
		this.passwordEncoder = passwordEncoder;
		this.maMessageDAO = maMessageDAO;
	}

	// @TODO 백오피스 기능으로 빼야 함.
	public Map createService(Map params) {
		String serviceCode = (String)params.get("serviceCode");

		///////////////// KEY 생성 /////////////////////
		log.info("####### enckey : {}", EncSHA256.doCryption(enckey));
		String apiKey = "messageGate_20240307_09142212_" + serviceCode;

		String encApiKey = AesUtil.doEncrypt(EncSHA256.doCryption(enckey), apiKey);
		String decApiKey = AesUtil.doDecrypt(EncSHA256.doCryption(enckey), encApiKey);
		String[] hashApiKey = passwordEncoder.encode(encApiKey).split("\\$");

		log.info("####### encApikey(고객제공) : {}", encApiKey); // hash 이전의 암호화 key 값 전달
		log.info("####### hashApiKey(DB저장) : {}", hashApiKey[0]);
		log.info("####### hash SALT(DB저장) : {}", hashApiKey[1]);

		log.info("####### key 검증  hashApiKey == Hash(encApikey + SALT) >>>>> {} == {},{} ", hashApiKey[0],
			encApiKey, hashApiKey[1]);

		Map dataParams = new HashMap();
		dataParams.put("apiKey", hashApiKey[0]); // encApiKey + ramdom salt
		dataParams.put("salt", hashApiKey[1]); // hash처리에서 사용된 ramdom salt값
		dataParams.put("serviceCode", serviceCode);
		dataParams.put("useYn", "Y");
		dataParams.put("createDate", "20240311");
		dataParams.put("expireDate", "99990101");

		try {
			// 기존 key 변경 이력 등록
			maMessageDAO.insertReHstServiceInfo(dataParams);

			// 기존 key 삭제
			maMessageDAO.removeServiceInfo(dataParams);

			// 신규 키 등록
			maMessageDAO.insertServiceInfo(dataParams);
		} catch (Exception e) {
			throw new RuntimeException("Create Service Error");
		}

		Map result = new HashMap();
		result.put("apikey", encApiKey);
		return result;

	}

}
