package com.bwc.common.rest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bwc.common.constant.CommonCode;
import com.bwc.common.util.StrUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * GateWay 전송
 * </pre>
 *
 * @ClassName   : RestTransUtil.java
 * @Description : GateWay 전송
 * @author BWC086
 * @since 2018. 8. 24.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018. 8. 24.     BWC086     	최초 생성
 * </pre>
 */

@Slf4j
@Service
public class RestTransUtil {

	@Autowired
	RestTemplateHttp restTemplate;

	@SuppressWarnings("unchecked")
	public JSONObject restSend(String Url, Object inDataSet) {
		JSONObject dataSet = new JSONObject();

		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		String sessGuid = StrUtil.nullToStr(session.getAttribute("SESS_GUID"));
		String httpUrl = "https://localhost:8081";

		log.info("SESS_GUID = {}, @RestAPI : restSend() ####START####", sessGuid);

		//        if(sysEnvCd.equals("D") || sysEnvCd.equals("L")){ // 개발
		//            httpUrl = "https://tooke.bluewalnut.co.kr";
		//        }
		//
		//        if(sysEnvCd.equals("P")) { // 운영
		//            httpUrl = "https://tooka.bluewalnut.co.kr";
		//        }

		String gwUrl = httpUrl + Url;

		try {
			log.info("SESS_GUID = {}, @Before : restSend() Url : {}, param : {}", sessGuid, gwUrl, inDataSet);

			dataSet = restTemplate.getHttpsTemplate().postForObject(gwUrl, inDataSet, JSONObject.class);

			log.info("SESS_GUID = {}, @After : restSend() returnData : {}", sessGuid, dataSet);

		} catch (RestClientException e) {
			log.error("SESS_GUID = {}, restSend() Exception : {}", sessGuid, "RestTransUril.RestClientException");

			dataSet.put("rsltCd", CommonCode.INTERNAL_API_ERROR.getCode());
			dataSet.put("rsltMsg", CommonCode.INTERNAL_API_ERROR.getMsg());

			log.error("SESS_GUID = {}, StackTrace : {}", sessGuid, StrUtil.getError(e));
		} catch (Exception e) {
			/*
			 * [CWE-209] Coderay 취약점
			 * e.getMessage() 제거
			 * */
			log.error("SESS_GUID = {}, restSend() Exception : {}", sessGuid, "Exception");

			dataSet.put("rsltCd", CommonCode.ETC_ERROR.getCode());
			dataSet.put("rsltMsg", CommonCode.ETC_ERROR.getMsg());

			log.error("SESS_GUID = {}, StackTrace : {}", sessGuid, StrUtil.getError(e));
		}

		log.info("SESS_GUID = {}, @RestAPI : restSend() ####E N D####", sessGuid);

		return dataSet;
	}
}
