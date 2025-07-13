package com.bwc.common.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import com.bwc.common.rest.RestTemplateHttp;
import com.bwc.common.vo.HMap;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ApiTransUtils.java
 * @Description : 클래스 설명을 기술합니다.
 * @author bwc205
 * @since 2021. 10. 6.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2021. 10. 6.     bwc205     	최초 생성
 * </pre>
 */
@Slf4j
@Service
public class ApiTransUtils {

	@Autowired
	private RestTemplateHttp restTemplate;

	/**
	 *
	 * Api 요청
	 *
	 * @param url
	 * @param Authorization
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject apiSendPost(String endpoint, HMap params) {
		//        String authKey = "Basic "+ Base64.encodeBase64String((affcSubId+":"+resultMap.getString("apiKey")).getBytes());
		String Authorization = "Basic " + "";

		log.debug("===============================");
		log.debug("Api 요청 endpoint : " + endpoint);
		log.debug("Authorization : " + Authorization);
		log.debug("parameter : " + params.toString());
		log.debug("===============================");

		JSONObject result = new JSONObject();
		String ApiUrl = "https://localhost:8081";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", Authorization);

		HttpEntity<Object> httpEntity = new HttpEntity<>(params, headers);

		try {
			result = restTemplate.getHttpsTemplate().postForObject(ApiUrl + endpoint, httpEntity, JSONObject.class);
		} catch (HttpStatusCodeException hsce) {
			log.debug("===============================");
			log.debug("e : {} ", hsce.getResponseBodyAsString());
			log.debug("===============================");

			try {
				JSONParser jsonStr = new JSONParser();
				Object obj = jsonStr.parse(hsce.getResponseBodyAsString());
				JSONObject jsonObj = (JSONObject)obj;

				String code = (String)jsonObj.get("resultCode");
				String msg = (String)jsonObj.get("resultMessage");

				result.put("resultCode", code);
				result.put("resultMessage", msg);
			} catch (Exception e) {
				log.debug("============== e : {}", e);
			}
		} catch (Exception e) {
			log.debug("========== {} =========", e);
			// log.debug(e.getMessage());
			// e.printStackTrace();
		}

		return result;
	}

	public JSONObject apiSendGet(String endpoint, HMap params) {
		//        String authKey = "Basic "+ Base64.encodeBase64String((affcSubId+":"+resultMap.getString("apiKey")).getBytes());
		String Authorization = "Basic " + "";

		log.debug("===============================");
		log.debug("Api 요청 endpoint : " + endpoint);
		log.debug("Authorization : " + Authorization);
		log.debug("parameter : " + params.toString());
		log.debug("===============================");

		JSONObject result = new JSONObject();
		String ApiUrl = "url";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", Authorization);

		HttpEntity<Object> httpEntity = new HttpEntity<>(params, headers);

		try {

			result = restTemplate.restTemplate()
				.exchange(ApiUrl + endpoint, HttpMethod.GET, httpEntity, JSONObject.class)
				.getBody();

		} catch (HttpStatusCodeException e) {
			log.debug("===============================");
			log.debug("e : {} ", e.getResponseBodyAsString());
			log.debug("===============================");

			try {
				JSONParser jsonStr = new JSONParser();
				Object obj = jsonStr.parse(e.getResponseBodyAsString());
				JSONObject jsonObj = (JSONObject)obj;

				String code = (String)jsonObj.get("resultCode");
				String msg = (String)jsonObj.get("resultMessage");

				result.put("resultCode", code);
				result.put("resultMessage", msg);
			} catch (Exception e1) {
				log.debug("============== e : {}", e1);
				//                result.put("resultMessage", ErrorCodeConstant.getRsltMsg(ErrorCodeConstant.getSystemApiError()));
			}

		} catch (Exception e) {
			log.debug("========== {} =========", e);
			// log.debug(e.getMessage());
			// e.printStackTrace();
		}

		return result;
	}

}
