package com.bwc.common.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 클라이언트 정보
 * </pre>
 *
 * @ClassName   : ClientUtils.java
 * @Description : 클라이언트 정보 관리
 * @author choi young kwang
 * @since 2018. 8. 20.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018. 8. 20.     choi young kwang       최초 생성
 * </pre>
 */

public class ClientUtils {

	/**
	 *
	 * Client IP 반환
	 *
	 * @param request
	 * @return
	 */
	public static String getRemoteIP(HttpServletRequest request) {
		//String ip = request.getHeader("X-FORWARDED-FOR");
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 *
	 * 디바이스 코드 반환
	 *
	 * @param request
	 * @return
	 */
	public static String getDeviceCd(HttpServletRequest request) {
		String dvceCd = StrUtil.EMPTY;
		if (request.getHeader("User-Agent").indexOf("Mobile") > -1
			|| request.getHeader("User-Agent").indexOf("Android") > -1
			|| request.getHeader("User-Agent").indexOf("iPhone") > -1) {
			dvceCd = "MO";
		} else {
			dvceCd = "PC";
		}

		return dvceCd;
	}

	/**
	 * 단말기가 모바일인지여부 반환
	 * Statements
	 *
	 * @param request
	 * @return
	 */
	public static boolean isMoblie(HttpServletRequest request) {

		String agent = (String)request.getHeader("User-Agent");
		String[] mobileos = {"iPhone", "iPod", "iPad", "Android", "BlackBerry", "Windows CE", "Nokia", "Webos",
			"Opera Mini", "SonyEricsson", "Opera Mobi", "IEMobile"};
		boolean isMobile = false;
		for (int i = 0; i < mobileos.length; i++) {
			if (agent.indexOf(mobileos[i]) > -1) {
				isMobile = true;
				break;
			}
		}
		return isMobile;
	}

	/**
	 * OS 정보 조회
	 *
	 * @param request
	 * @return
	 */
	public static String getOs(HttpServletRequest request) {

		String agent = (String)request.getHeader("User-Agent");
		String os = "";

		if (isMoblie(request)) { //모바일

			if (agent.indexOf("iPhone") > -1 || agent.indexOf("iPod") > -1 || agent.indexOf("iPad") > -1) {
				os = "iOS";
			} else {
				os = "Android";
			}

		} else {

			if (agent.indexOf("Macintosh") > -1 || agent.indexOf("Mac") > -1) {
				os = "MAC";
			} else {
				os = "Windows";
			}
		}

		return os;
	}

	/**
	 *
	 * TrxGuid 구하기
	 *
	 * @param requestUrl
	 * @return
	 */
	public static String getTrxGuid(String requestUrl) {

		String trxGuid = StringUtils.EMPTY;
		String prefix = "/pay/";
		String notPrefix = "/pay/order/";

		if (requestUrl.indexOf(prefix) == 0 && !requestUrl.startsWith(notPrefix)) {
			String[] arrUrl = requestUrl.split("/");
			if (!"transactions".equals(arrUrl[2])) {
				trxGuid = arrUrl[2];
			}
		}

		return trxGuid;
	}

	/**
	 *
	 * 주소에서 축약 명 구하기
	 *
	 * @param requestUrl
	 * @param name
	 * @return
	 */
	public static String changeExecNm(String requestUrl, String name) {

		String[] arrUrl = requestUrl.split("/");
		String result = arrUrl[arrUrl.length - 1];
		;

		if (requestUrl.indexOf("/pay/") == 0 &&
			arrUrl.length == 3 &&
			!"transactions".equals(result)) {
			result = "pay";
		}

		return result;
	}

}
