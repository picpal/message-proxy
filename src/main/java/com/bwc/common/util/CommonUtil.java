package com.bwc.common.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.util.Base64;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;

import com.bwc.common.constant.Constant;
import com.bwc.common.exception.BaseException;
import com.bwc.common.exception.SessionException;
import com.bwc.common.vo.HMap;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 프로젝트 전반에 사용되는 공통 Util Class
 *
 * @author lucifer
 * @version 1.0
 * @ClassName CommonUtil.java
 * @Description CommonUtil Class
 * @Modification-Information <pre>
 *    수정일       수정자              수정내용
 *  ----------   ----------   -------------------------------
 *  2020. 7. 1.    lucifer      최초생성
 * </pre>
 * @see <pre>
 *  Copyright (C) 2020 by Taihoinst CO.,LTD. All right reserved.
 * </pre>
 * @since 2020. 7. 1.
 */
@Slf4j
public final class CommonUtil {

	private CommonUtil() {
	}

	/**
	 * Object의 null을 체크하고 값이 없으면 초기값을 넘겨준다.
	 *
	 * @param o            Null을 체크할 Object
	 * @param defaultValue 값이 없을 때 넘겨줄 초기값
	 * @return Null을 체크하여 결과값을 리턴한다.
	 */
	public static String checkNull(
		Object o,
		String defaultValue) {
		String returnValue = checkNull(o);
		if (returnValue.equals("")) {
			return defaultValue;
		} else {
			return returnValue;
		}
	}

	/**
	 * Object의 null을 체크하고 값이 없으면 초기값을 넘겨준다.
	 *
	 * @param o            Null을 체크할 Object
	 * @param defaultValue 값이 없을 때 넘겨줄 초기값
	 * @return Null을 체크하여 결과값을 리턴한다.
	 */
	public static int checkNull(
		Object o,
		int defaultValue) {
		String returnValue = checkNull(o);
		if (returnValue.equals("")) {
			return defaultValue;
		} else {
			return Integer.parseInt(returnValue);
		}
	}

	/**
	 * Object의 null을 체크하고 값이 없으면 초기값을 넘겨준다.
	 *
	 * @param o            Null을 체크할 Object
	 * @param defaultValue 값이 없을 때 넘겨줄 초기값
	 * @return Null을 체크하여 결과값을 리턴한다.
	 */
	public static long checkNullLong(
		Object o,
		long defaultValue) {
		String returnValue = checkNull(o);
		if (returnValue.equals("")) {
			return defaultValue;
		} else {
			return Long.parseLong(returnValue);
		}
	}

	/**
	 * Object의 null을 체크하고 값이 없으면 초기값을 넘겨준다.
	 *
	 * @param o            Null을 체크할 Object
	 * @param defaultValue 값이 없을 때 넘겨줄 초기값
	 * @return Null을 체크하여 결과값을 리턴한다.
	 */
	public static double checkNull(
		Object o,
		double defaultValue) {
		String returnValue = checkNull(o);
		if (returnValue.equals("")) {
			return defaultValue;
		} else {
			return Double.parseDouble(returnValue);
		}
	}

	/**
	 * 문자열의 null을 체크하고 값이 없으면 초기값을 넘겨준다. HTML 변환이 필요한 곳에서 사용한다.
	 *
	 * @param s            Null을 체크할 문자열
	 * @param defaultValue 값이 없을 때 넘겨줄 초기값
	 * @return Null을 체크하여 결과값을 리턴한다.new String(returnValue.getBytes("8859_1"),
	 * "UTF-8");
	 */
	public static String checkNull(
		String s,
		String defaultValue) {
		String s1 = null;
		if (s == null) {
			s1 = defaultValue;
		} else if (s.equals("")) {
			s1 = defaultValue;
		} else {
			s1 = s.trim();
		}
		return s1;
	}

	/**
	 * 문자열의 null을 체크하고 값이 없으면 초기값을 넘겨준다.
	 *
	 * @param s            Null을 체크할 문자열
	 * @param defaultValue 값이 없을 때 넘겨줄 초기값
	 * @return Null을 체크하여 결과값을 리턴한다.
	 */
	public static int checkNull(
		String s,
		int defaultValue) {
		int s1 = 0;

		if (s == null) {
			s1 = defaultValue;
		} else if (s.equals("")) {
			s1 = defaultValue;
		} else {
			s1 = Integer.valueOf(s);
		}
		return s1;
	}

	/**
	 * 문자열의 null을 체크하고 값이 없으면 초기값을 넘겨준다. jsp에서 POST방식으로 폼 전송시 한글이 깨지는경우가 발생하여 추가
	 *
	 * @param s
	 * @param defaultValue
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String checkNull(
		String s,
		String defaultValue,
		String encoding) throws UnsupportedEncodingException {
		String s1 = checkNull(s, defaultValue);
		return URLDecoder.decode(s1, encoding);
	}

	/**
	 * 문자열의 null을 체크하고 값이 없으면 초기값을 넘겨준다. jsp에서 POST방식으로 폼 전송시 한글이 깨지는경우가 발생하여 추가
	 *
	 * @param s
	 * @param defaultValue
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String checkNull(
		Object s,
		String defaultValue,
		String encoding) throws UnsupportedEncodingException {
		String s1 = checkNull(s, defaultValue);
		return URLDecoder.decode(s1, encoding);
	}

	/**
	 * Object의 null을 체크한다.
	 *
	 * @param s Null을 체크할 문자열
	 * @return Null을 체크하여 결과값을 리턴한다.
	 */
	public static String checkNull(
		Object o) {
		Object returnValue = o;

		if (returnValue == null) {
			return "";
		} else if (returnValue instanceof String) {
			return (String)returnValue;
		} else if (returnValue instanceof Integer) {
			return returnValue.toString();
		} else if (returnValue instanceof BigDecimal) {
			return returnValue.toString();
		} else if (returnValue instanceof Double) {
			return returnValue.toString();
		} else if (returnValue instanceof String[]) {
			String[] value = (String[])returnValue;
			return value[0];
		} else {
			return returnValue.toString();
		}
	}

	/**
	 * WEB에서 FileUpload Ajax 사용 시 Spring에서 Return 값 전송
	 */
    /*public static ModelAndView ajaxJsonSend(
            Object retVal ) {
        ModelAndView mv = new ModelAndView(new JSONView());
        mv.addObject("jobj", new Gson().toJson(retVal));

        return mv;
    }*/

	/**
	 * JAVA단에서 Jsp로 메세지 출력
	 *
	 * @param message
	 * @param response
	 * @throws IOException
	 * @throws Exception
	 */
	public static void alertScript(
		String message,
		HttpServletResponse response) throws IOException {
		response.reset();
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print("<script language='JavaScript'>alert(\"" + message.replaceAll("(\r\n|\n\r|\r|\n)", "\\\\n")
				+ "\"); history.back(-1);</script>");
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("alertScript Exception");
		} finally {
			if (out != null)
				out.close();
		}
	}

	/**
	 * CamelCase to UnderScope 변환
	 *
	 * @param camelCase
	 * @return
	 */
	public static String toUnderScope(
		String camelCase) {
		return camelCase.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toUpperCase();
	}

	/**
	 * CamelCase 변환
	 *
	 * @param underScore
	 * @return
	 */
	public static String toCamelCase(
		String underScore) {
		if (underScore.indexOf('_') < 0 && Character.isLowerCase(underScore.charAt(0)))
			return underScore;
		StringBuilder result = new StringBuilder();
		boolean nextUpper = false;
		int len = underScore.length();
		for (int i = 0; i < len; i++) {
			char currentChar = underScore.charAt(i);
			if (currentChar == '_') {
				nextUpper = true;
				continue;
			}
			if (nextUpper) {
				result.append(Character.toUpperCase(currentChar));
				nextUpper = false;
			} else {
				result.append(Character.toLowerCase(currentChar));
			}
		}

		return result.toString();
	}

	/**
	 * 변환된 html Tab를 다시 원래대로 변환한다.
	 *
	 * @param s
	 * @return
	 */
	public static String chgConversion(
		String s) {
		String retVal = s;
		if (s == null)
			return "";

		return retVal.replaceAll("&lt;", "<")
			.replaceAll("&gt;", ">")
			.replaceAll("&amp;", "&")
			.replaceAll("&quot;", "\"")
			.replaceAll("&apos;", "\'")
			.replaceAll("\n", "<br/>");
	}

	/**
	 * br Tab를 개행문자로 변환 - textarea 에 필요
	 *
	 * @param s
	 * @return
	 */
	public static String chgBrConversion(
		String s) {
		String retVal = s;
		if (s == null)
			return "";

		return retVal.replaceAll("<br/>", "\n");
	}

	/**
	 * Xss 변환 시킨다.
	 *
	 * @param strVal
	 * @return
	 */
	public static String chgXssConversion(String strVal) {
		String s = strVal;
		if (s == null)
			return "";
		String retVal = "";

		// Remove all sections that match a pattern
		for (Pattern scriptPattern : Constant.PATTERN) {
			s = scriptPattern.matcher(s).replaceAll("");
		}

		retVal = s;
		return retVal;
	}

	/**
	 * Xss 변환된 값을 Map으로 Return
	 *
	 * @param oriMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> convXssData(Map<String, Object> oriMap) {
		Map<String, Object> targetMap = new HashMap<>();
		Iterator<String> keyIterator = oriMap.keySet().iterator();
		String keyName = "";
		String mapValues = "";
		while (keyIterator.hasNext()) {
			keyName = keyIterator.next();
			if (oriMap.get(keyName) instanceof Integer)
				continue;
			if (oriMap.get(keyName) instanceof BigDecimal)
				continue;
			if (oriMap.get(keyName) instanceof Double)
				continue;
			// value가 map이면 재귀호출
			if (oriMap.get(keyName) instanceof Map) {
				//LOGGER.debug("CommonUtil.convXssData Map before >>>> " + oriMap.get(keyName).toString());
				targetMap.put(keyName, convXssData((Map<String, Object>)oriMap.get(keyName)));
				//LOGGER.debug("CommonUtil.convXssData Map after >>>> " + targetMap.get(keyName).toString());
			} else {
				mapValues = CommonUtil.checkNull(oriMap.get(keyName));
				//LOGGER.debug("CommonUtil.convXssData else before >>>> " + mapValues);
				targetMap.put(keyName, CommonUtil.chgXssConversion(mapValues));
				//LOGGER.debug("CommonUtil.convXssData else after >>>> " + targetMap.get(keyName).toString());
			}
		}

		return targetMap;
	}

	/**
	 * 브라우저 판별 메서드
	 *
	 * @param request
	 * @return
	 */
	public static String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		// IE11부터 MSIE가 Trident로 변경
		if (header.indexOf("MSIE") > -1 || header.indexOf("Trident") > -1 || header.indexOf("Edge") > -1) {
			return "MSIE";
		} else if (header.indexOf("Chrome") > -1) {
			return "Chrome";
		} else if (header.indexOf("Opera") > -1) {
			return "Opera";
		} else if (header.indexOf("Safari") > -1) {
			return "Safari";
		}
		return "Firefox";
	}

	/**
	 * 브라우저별 파일명 인코딩 변환
	 *
	 * @param request
	 * @param fileName
	 * @return
	 */
	public static String getDownFileNames(HttpServletRequest request, String pFileName) {
		String fileName = pFileName;
		String browser = getBrowser(request);
		if (fileName == null || fileName.equals("")) {
			fileName = "UnKnownFileName";
		}

		String resultName = "";
		try {
			// Explorer
			if (browser.indexOf("MSIE") != -1) {
				resultName = URLEncoder.encode(fileName, "utf-8").replaceAll(" ", "%20").replaceAll("[+]", "%20");
			}
			// Opera
			else if (browser.indexOf("Opera") != -1) {
				resultName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			// Chrome
			else if (browser.indexOf("Chrome") != -1) {
				resultName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			// Safari
			else if (browser.indexOf("Safari") != -1) {
				resultName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			// FireFox
			else if (browser.indexOf("Firefox") != -1) {
				resultName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			// Other
			else {
				resultName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
		} catch (Exception ex) {
			resultName = fileName;
		}

		return resultName;
	}

	/**
	 * 문자 금액에 콤마를 삽입한다.
	 *
	 * @param amt 변환할 금액
	 * @return String
	 * <p>
	 *
	 * <pre>
	 *  - 사용 예
	 *        String date = CommonUtil.getCommaNumber("123456")
	 *  결과 : 123,456"
	 *         </pre>
	 */
	public static String getCommaNumber(String amt) {
		if (CommonUtil.checkNull(amt, "").equals(""))
			return "0";
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(0);
		return nf.format(stringToNumber(amt));
	}

	/**
	 * 금액에 콤마를 삽입한다.
	 *
	 * @param amt 변환할 금액
	 * @param dec 소수자리
	 * @return String
	 * <p>
	 *
	 * <pre>
	 *  - 사용 예
	 *        String date = CommonUtil.getCommaNumber(123456.123, 3)
	 *  결과 : 123,456.123
	 *         </pre>
	 */
	public static String getCommaNumber(
		double amt,
		int dec) {
		if (CommonUtil.checkNull(amt, "").equals(""))
			return "0";
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(dec);
		return nf.format(amt);
	}

	/**
	 * <pre>
	 * 숫자와 '.'가 아닌문자의 경우 문자를제외후 double형으로 리턴한다.
	 * </pre>
	 *
	 * @param str
	 * @return double - 사용 예 String num =
	 * CommonUtil.stringToNumber("####1163.51244***123#####") 결과 :
	 * 1163.51244123
	 */
	public static double stringToNumber(
		String str) {
		if (CommonUtil.checkNull(str, "").equals(""))
			return 0;
		StringBuffer sb = new StringBuffer();
		String number = "-1234567890.";

		for (int i = 0; i < str.length(); i++) {
			if (number.indexOf(str.charAt(i)) > -1) {
				sb.append(str.charAt(i));
			}
		}

		return Double.parseDouble(sb.toString());
	}

	/**
	 * 파일명으로 허용되지 않은 확장자를 체크하여 결과를 리턴한다.
	 *
	 * @param contentType  업로드된 파일명
	 * @param notAllowFile 걸러질 파일 확장자(asp,jsp...)
	 * @return
	 */
	public static boolean contentTypeValidate(
		String contentType,
		String notAllowFile) {
		if ("".equals(CommonUtil.checkNull(contentType)))
			return false;

		// 파일확장자 구하기
		int pos = contentType.lastIndexOf('.');
		String ext = contentType.substring(pos + 1);

		String[] fileExt = notAllowFile.split("[,]");

		if (fileExt != null) {
			for (int i = 0; i < fileExt.length; i++) {
				if (ext.equals(fileExt[i])) {
					return false;
				}
			}

			return true;
		}

		return true;
	}

	/**
	 * 파일명으로 허용되는 확장자를 체크하여 결과를 리턴
	 *
	 * @param contentType : 업로드된 파일명
	 * @param allowFile   : 허용되는 확장자(jpg,jpeg,png,tif,pdf...)
	 * @return
	 */
	public static boolean contentTypeAllowValidate(
		String contentType,
		String allowFile) {
		if ("".equals(CommonUtil.checkNull(contentType)))
			return false;
		if ("".equals(CommonUtil.checkNull(allowFile)))
			return true;

		// 파일확장자 구하기
		int pos = contentType.lastIndexOf('.');
		String ext = contentType.substring(pos + 1);

		String[] fileExt = allowFile.split("[,]");
		if (fileExt != null) {
			for (int i = 0; i < fileExt.length; i++) {
				if (ext.equals(fileExt[i])) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * 문자열 일정자리수 이상인경우 mark ex) getCutString("가나다라마바사","...",5) >> 가나다라마...
	 *
	 * @return
	 */
	public static String cutString(
		String str,
		String mark,
		int len) {
		String tmp = "";
		if (str.length() > len) {
			tmp = str.substring(0, len) + mark;
		} else {
			tmp = str;
		}
		return tmp;
	}

	/**
	 * 핸드폰번호 포멧 리턴
	 *
	 * @return
	 */
	public static String formatPhoneNo(
		String pPhoneNumber) {
		String phoneNumber = pPhoneNumber;
		if (CommonUtil.checkNull(phoneNumber, "").equals(""))
			return "";

		String regEx = "(\\d{3})(\\d{3,4})(\\d{4})";

		if (phoneNumber.length() > 1) {
			phoneNumber = phoneNumber.replaceAll("\\D+", "");
		} else {
			phoneNumber = phoneNumber.replace("\\D+", "");
		}

		if (phoneNumber.equals("")) {
			return "";
		}
		if (!Pattern.matches(regEx, phoneNumber)) {
			return phoneNumber;
		}

		return phoneNumber.replaceAll(regEx, "$1-$2-$3");
	}

	/**
	 * 전화번호 포멧 리턴
	 *
	 * @return
	 */
	public static String formatTelNo(
		String pTelNumber) {
		String telNumber = pTelNumber;
		if (CommonUtil.checkNull(telNumber, "").equals(""))
			return "";

		String regEx = "(\\d{2,3})(\\d{3,4})(\\d{4})";
		String regEx2 = "(\\d{2})(\\d{3,4})(\\d{4})"; // 서울

		if (telNumber.length() > 1) {
			telNumber = telNumber.replaceAll("\\D+", "");
		} else {
			telNumber = telNumber.replace("\\D+", "");
		}

		if (telNumber.equals(""))
			return "";
		if (telNumber.substring(1, 2).equals("2")) {
			if (!Pattern.matches(regEx2, telNumber)) {
				return telNumber;
			}

			return telNumber.replaceAll(regEx2, "$1-$2-$3");
		} else {
			if (!Pattern.matches(regEx, telNumber)) {
				return telNumber;
			}

			return telNumber.replaceAll(regEx, "$1-$2-$3");
		}
	}

	/**
	 * 팩스번호 포멧 리턴
	 *
	 * @param faxNumber
	 * @return
	 */
	public static String formatFaxNo(String faxNumber) {
		String regEx = "(\\d{2})(\\d{2})(\\d{3,4})(\\d{4})";
		String regEx2 = "(\\d{2})(\\d{1})(\\d{3,4})(\\d{4})"; // 서울

		String nFaxNumber = faxNumber;
		if (CommonUtil.checkNull(nFaxNumber, "").equals(""))
			return ""; // 공백이면
		if (nFaxNumber.length() > 1) {
			nFaxNumber = nFaxNumber.replaceAll("\\D+", ""); // 숫자만
		} else {
			nFaxNumber = nFaxNumber.replace("\\D+", "");
		}
		if (nFaxNumber.equals("")) {
			return "";
		} // 공백이면

		// 번외
		if (nFaxNumber.substring(0, 3).equals("070") || nFaxNumber.substring(0, 4).equals("1544")
			|| nFaxNumber.substring(0, 4).equals("0505") || nFaxNumber.substring(0, 4).equals("1644")
			|| nFaxNumber.substring(0, 4).equals("1800")) {
			return faxNumber;

			// 국내
		} else if (nFaxNumber.substring(0, 2).equals("82") || nFaxNumber.substring(0, 1).equals("0")) {
			nFaxNumber = (nFaxNumber.substring(0, 3).equals("820")) ? nFaxNumber.substring(3) : nFaxNumber;
			nFaxNumber = (nFaxNumber.substring(0, 2).equals("82")) ? nFaxNumber.substring(2) : nFaxNumber;
			nFaxNumber = (nFaxNumber.substring(0, 1).equals("0")) ? nFaxNumber.substring(1) : nFaxNumber;
			nFaxNumber = "82" + nFaxNumber;

			if (nFaxNumber.substring(2, 3).equals("2")) {
				if (!Pattern.matches(regEx2, nFaxNumber)) {
					return nFaxNumber;
				}

				return nFaxNumber.replaceAll(regEx2, "$1-$2-$3-$4");
			} else {
				if (!Pattern.matches(regEx, nFaxNumber)) {
					return nFaxNumber;
				}

				return nFaxNumber.replaceAll(regEx, "$1-$2-$3-$4");
			}
		}
		// 국외(입력값리턴)
		else {
			return faxNumber;
		}
	}

	/**
	 * 랜덤한 숫자타입의 문자열을 원하는 길이만큼 반환한다.
	 *
	 * @param length 문자열 길이
	 * @return 랜덤문자열
	 */
	public static String getRandomString(int length) {
		String randomStr = "";
		SecureRandom random = new SecureRandom();

		for (int i = 0; i < length; i++) {
			// 0 ~ 9 랜덤 숫자 생성
			randomStr += random.nextInt(10);
		}

		return randomStr;
	}

	/**
	 * 고정크기의 문자 왼쪽에 특정문자 채우기
	 *
	 * @param source
	 * @param padString
	 * @param len
	 */
	public static String lpad(String source, String padString, int len) {
		StringBuffer sBuf = new StringBuffer();

		try {
			if (source.length() >= len) {
				return source;
			}
			for (int i = 0; i < len - source.length(); i++) {
				sBuf = sBuf.append(padString);
			}
		} catch (Exception e) {
			// System.out.println(e);
			return "ERROR";
		}
		return sBuf.append(source).toString();
	}

	/**
	 * 문자열에서 특정갯수만큼 자른다.(갯수만큼 잘라서 가져온다.)
	 *
	 * @param str     특정부분을 자를 문자열
	 * @param start 자를 시작위치
	 * @param count 자를 문자갯수
	 * @return 시작위치에서 특정갯수만큼 잘려진 string 값
	 */
	public static String midString(String str, int start, int count) throws Exception {
		if (str == null)
			return null;
		String result = null;
		try {
			if (start >= str.length()) {
				result = "";
			} else if (str.length() < start + count) {
				result = str.substring(start, str.length());
			} else {
				result = str.substring(start, start + count);
			}
		} catch (Exception ex) {
			log.error("CommonUtil.midString(\"" + str + "\"," + start + "," + count + ")\r\n" + "midString Exception");
		}
		return result;
	}

	/**
	 * 문자열의 오른쪽부터 특정갯수만큼 문자를 자른다.
	 *
	 * @param str   특정부분을 자를 문자열
	 * @param count 자를 문자갯수
	 * @return 오른쪽부터 특정갯수만큼 잘려진 string 값
	 */
	public static String rightString(String str, int count) throws Exception {
		if (str == null)
			return null;
		if (str.equals(""))
			return "";
		String result = null;
		try {
			if (count == 0) {// 갯수가 0 이면 공백을
				result = "";
			} else if (count > str.length()) { // 문자열 길이보다 크면 문자열 전체를
				result = str;
			} else {
				result = str.substring(str.length() - count, str.length()); // 오른쪽
				// count
				// 만큼 리턴
			}
		} catch (Exception ex) {
			log.error("CommonUtil.rightString(\"" + str + "\"," + count + ")\r\n" + "Exception");
		}
		return result;
	}

	/**
	 * 문자열의 왼쪽부터 특정갯수만큼 문자를 자른다.
	 *
	 * @param str     특정부분을 자를 문자열
	 * @param count 자를 문자갯수
	 * @return 왼쪽부터 특정갯수만큼 잘려진 string 값
	 */
	public static String leftString(String str, int count) throws Exception {
		if (str == null)
			return null;
		if (str.equals(""))
			return "";
		String result = null;
		try {
			if (count == 0) { // 갯수가 0 이면 공백을
				result = "";
			} else if (count > str.length()) { // 문자열 길이보다 크면 문자열 전체를
				result = str;
			} else {
				result = str.substring(0, count); // 왼쪽 count 만큼 리턴
			}
		} catch (Exception ex) {
			log.error("CommonUtil.leftString(\"" + str + "\"," + count + ")\r\n" + "Exception");
		}
		return result;
	}

	/**
	 * Full경로 파일명에서 파일명만 추출 - 확장자 포함
	 *
	 * @return
	 */
	public static String getFileName(String fullPachFilename) {
		if ("".equals(CommonUtil.checkNull(fullPachFilename)))
			return "";

		int file = fullPachFilename.lastIndexOf('/');
		// int ext = fullPachFilename.lastIndexOf(".");
		int length = fullPachFilename.length();
		String filename = fullPachFilename.substring(file + 1, length);

		return filename;
	}

	/**
	 * Full경로 파일명에서 확장자만 추출
	 *
	 * @return
	 */
	public static String getFileExt(String fullPachFilename) {
		if ("".equals(CommonUtil.checkNull(fullPachFilename)))
			return "";

		int ext = fullPachFilename.lastIndexOf('.');
		int length = fullPachFilename.length();
		String filenameExt = "";
		if (ext != -1) {
			filenameExt = fullPachFilename.substring(ext + 1, length);
		}

		return filenameExt;
	}

	/**
	 * Full경로 파일명에서 파일명만 추출 - 확장자 제외
	 *
	 * @return
	 */
	public static String getFileNameOnly(String fullPachFilename) {
		if ("".equals(CommonUtil.checkNull(fullPachFilename)))
			return "";

		int file = fullPachFilename.lastIndexOf('/');
		int ext = fullPachFilename.lastIndexOf('.');
		String filename = "";
		if (ext != -1) {
			filename = fullPachFilename.substring(file + 1, ext);
		} else {
			filename = fullPachFilename;
		}

		return filename;
	}

	/**
	 * 파일명 변조 방지
	 *
	 * @return
	 */
	public static String replFileNameInjection(String fullPachFilename) {
		if ("".equals(CommonUtil.checkNull(fullPachFilename)))
			return "";

		String fileName = getFileNameOnly(fullPachFilename); // 파일명 추출
		String fileExt = getFileExt(fullPachFilename); // 확장자 추출
		String retFileName = "";

		// 파일명에서 특수문자 제외
		// fileName =
		// fileName.replaceAll("[\"'\\{\\}\\[\\]/?.,;:|\\)\\(*~`!^\\+<>&@#$%^\\\\=\\p{Space}]",
		// "");
		fileName = fileName.replaceAll("&", "&");
		fileName = fileName.replaceAll("<", "<");
		fileName = fileName.replaceAll(">", ">");
		fileName = fileName.replaceAll("\"", "&#34;");
		fileName = fileName.replaceAll("\'", "'");
		fileName = fileName.replaceAll("%00", null);
		fileName = fileName.replaceAll("%", "&#37;");
		if (!"".equals(fileExt)) {
			retFileName = fileName + "." + fileExt;
		} else {
			retFileName = fileName;
		}

		return retFileName;
	}

	/**
	 * 파일명 변경 - full경로 + 파일명(확장자포함)
	 *
	 * @return
	 */
	public static boolean setFileRename(String orgFullFileNm, String newFullFileNm) {
		if (checkNull(orgFullFileNm, "").equals("") || checkNull(newFullFileNm, "").equals("")) {
			return false;
		} else {
			File f = new File(orgFullFileNm);
			File t = new File(newFullFileNm);
			if (f.exists()) {
				f.renameTo(t);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Controller의 jsonView Return 제거
	 *
	 * @param model
	 * @param returnCode
	 * @param returnMsg
	 */
	public static void setReturnResult(Object model, String returnCode, String returnMsg) {
		if (model instanceof Model) {
			((Model)model).addAttribute("error_code", returnCode);
			((Model)model).addAttribute("error_message", returnMsg);
		} else if (model instanceof ModelMap) {
			((ModelMap)model).addAttribute("error_code", returnCode);
			((ModelMap)model).addAttribute("error_message", returnMsg);
		} else if (model instanceof ModelAndView) {
			((ModelAndView)model).addObject("error_code", returnCode);
			((ModelAndView)model).addObject("error_message", returnMsg);
		}
	}

	/**
	 * List HMap 형태의 Data를 JSON형태로 변환한다.
	 *
	 * @param resultList
	 * @return
	 * @throws Exception
	 */
	public static String makeEgovMapToJson(List<HMap> resultList) {
		StringBuffer convtJson = new StringBuffer();
		int tok = 0;
		HMap rsMap = new HMap();
		if (resultList != null) {
			convtJson.append("[");
			for (int i = 0; i < resultList.size(); i++) {
				convtJson.append(" {");
				rsMap = resultList.get(i);
				tok = 0;
				for (Object entry : rsMap.keySet()) {
					if (tok > 0)
						convtJson.append(",");
					convtJson.append(entry.toString() + ":'" + rsMap.get(entry) + "'");
					tok++;
				}
				convtJson.append("} ");
				if (i < (resultList.size() - 1))
					convtJson.append(",");
			}
			convtJson.append("]");
		}
		return convtJson.toString();
	}

	/**
	 * Code List객체를 Jqgrid Select Option으로 변환
	 *
	 * @return
	 */
	public static String convSelectOptionJqgrid(List<HMap> recordList, String allGubun) {
		String retVal = "";
		int cnt = 0;
		if (!allGubun.equals("")) {
			retVal = ":" + CommonUtil.checkNull(allGubun) + ";";
		}
		for (HMap hmap : recordList) {
			retVal += checkNull(hmap.get("cd")) + ":" + checkNull(hmap.get("cdNm"));
			if (cnt < recordList.size() - 1)
				retVal += ";";
			cnt++;
		}

		return retVal;
	}

	/**
	 * 32글자의 랜덤한 문자열(숫자포함)을 만들어서 반환
	 *
	 * @return
	 */
	public static String getRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 화면에서 넘어오는 grid 데이터를 list map으로 변환한다.
	 *
	 * @param paramKey
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> gridParamList(Object paramKey) throws
		JsonParseException,
		JsonMappingException,
		IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!CommonUtil.checkNull(paramKey).equals("")) {
			result = mapper.readValue(CommonUtil.checkNull(paramKey), List.class);
		}
		return result;
	}

	/**
	 * JSON 특수문자 처리
	 *
	 * @param str
	 * @return
	 */
	public static String toJS(String str) {
		return str.replace("\\", "\\\\")
			.replace("\'", "\\\'")
			.replace("\"", "\\\"")
			.replace("\r\n", "\\n")
			.replace("\n", "\\n");
	}

	/**
	 * path matcher 체크
	 *
	 * @param pathPattern
	 * @param pathName
	 * @param urlPostfix
	 * @return
	 */
	public static boolean pathMatcher(String pathPattern, String pathName, String urlPostfix) {
		boolean isSkip = false;
		String[] nosessionview = CommonUtil.checkNull(pathPattern).split("\\,");
		AntPathMatcher pathMatcher = new AntPathMatcher();
		for (int i = 0; i < nosessionview.length; i++) {
			if (pathMatcher.match(nosessionview[i], "/" + pathName)) {
				isSkip = true;
				break;
			}
			if (pathMatcher.match(nosessionview[i], "/" + pathName + urlPostfix)) {
				isSkip = true;
				break;
			}
		}
		return isSkip;
	}

	/**
	 * 공백 전체 제거
	 */
	public static String allTrim(String s) {
		return s.replaceAll("\\s+", "");
	}

	/**
	 * 시스템 로그를 출력한다.
	 *
	 * @param obj Object
	 * @throws Exception
	 */
	public static void debug(Object obj, String exGubun) throws Exception {
		if (obj instanceof java.lang.Exception) {
			String exMessage = ((Exception)obj).getMessage();
			log.debug("Debug Log : {}", exMessage);

			if ("base".equals(exGubun) || "".equals(exGubun)) {
				throw new BaseException(exMessage);
			} else if ("common".equals(exGubun)) {
				throw new Exception(exMessage);
			} else if ("injection".equals(exGubun)) {
				throw new Exception(exMessage);
			} else if ("session".equals(exGubun)) {
				throw new SessionException(exMessage);
			} else if ("json".equals(exGubun)) {
				throw new Exception(exMessage);
			} else {
				throw new BaseException(exMessage);
			}
		}
	}

	/**
	 * oriMap의 중복 키 제외
	 *
	 * @param oriMap
	 * @param srcMap
	 * @return
	 */
	public static Map<String, Object> putAll(Map<String, Object> oriMap, Map<String, Object> srcMap) {
		Map<String, Object> targetMap = new HashMap<String, Object>();
		Iterator<String> keyIterator = srcMap.keySet().iterator();
		String keyName = "";
		String mapValues = "";
		while (keyIterator.hasNext()) {
			keyName = keyIterator.next();
			if (!oriMap.containsKey(keyName)) {
				mapValues = CommonUtil.checkNull(srcMap.get(keyName));
				targetMap.put(keyName, CommonUtil.chgXssConversion(mapValues));
			}
		}
		return targetMap;
	}

	/**
	 * Exception Throwable stack trace를 String으로 변환
	 *
	 * @param e
	 * @return
	 */
	public static String exceptionThrowableToString(Exception e) {
		String strException = "";
		if (e.getCause() == null) {
			strException = ExceptionUtils.getStackTrace(e);
		} else {
			strException = ExceptionUtils.getStackTrace(e.getCause());
		}
		return strException;
	}

	/**
	 * Map계열의 데이터를 JSON형태로 변환 후 반환
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String mapToJsonString(Object param) throws Exception {
		return new ObjectMapper().writeValueAsString(param);
	}

	/**
	 * ClientKey 생성
	 *
	 * @return
	 */
	public static String genClientKey() {
		return CommonUtil.uuid64(CommonUtil.getUUID());
	}

	/**
	 * UUID 생성
	 *
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * UUID 생성 - Hyphen 제외
	 *
	 * @return
	 */
	public static String getUUIDNotHyphen() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * UUID를 base64 String으로 변환
	 *
	 * @param uuidStr
	 * @return
	 */
	public static String uuid64(
		String uuidStr) {
		UUID uuid = UUID.fromString(uuidStr);
		byte[] bytes = uuidToBytes(uuid);
		return Base64.encodeBase64StringUnChunked(bytes);
	}

	/**
	 * UUID를 Byte 형식으로 변환
	 *
	 * @param uuid
	 * @return
	 */
	public static byte[] uuidToBytes(
		UUID uuid) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		return byteBuffer.array();
	}

	/**
	 * Base64 encoding
	 *
	 * @param strValue
	 * @return
	 */
	public static String Base64Encode(String strValue) {
		byte[] encodedBytes = Base64.encodeBase64(strValue.getBytes(StandardCharsets.UTF_8));
		return new String(encodedBytes);
	}

	/**
	 * Base64 decoding
	 *
	 * @param strValue
	 * @return
	 */
	public static String Base64Decode(String strValue) {
		byte[] decodedBytes = Base64.decodeBase64(strValue.getBytes(StandardCharsets.UTF_8));
		return new String(decodedBytes);
	}

	/**
	 * HttpServletRequest to Map Convert
	 *
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> requestToMap(HttpServletRequest request) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		Enumeration enums = request.getParameterNames();
		while (enums.hasMoreElements()) {
			String paramKey = (String)enums.nextElement();
			String[] parameters = request.getParameterValues(paramKey);
			if (parameters.length > 1) {
				parameterMap.put(paramKey, parameters);
			} else {
				parameterMap.put(paramKey, parameters[0]);
			}
		}
		return parameterMap;
	}

	/**
	 * String To JsonObject convert
	 *
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	public static Object stringToJson(String str) throws ParseException {
		JSONParser parser = new JSONParser();
		Object returnObj = parser.parse(str);
		return returnObj;
	}

	/**
	 * JSON String의 JSON 형식 체크
	 *
	 * @param jsonString
	 * @return
	 * @throws IOException
	 */
	public static boolean isJSONValid(String jsonString) {
		boolean isJsonType = true;
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonString);
		} catch (IOException ex) {
			isJsonType = false;
		}
		return isJsonType;
	}

}
