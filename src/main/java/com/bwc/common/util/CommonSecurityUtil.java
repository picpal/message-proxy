package com.bwc.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 교차접속 스크립트 공격 취약성 방지(파라미터 문자열 교체)
 * </pre>
 *
 * @ClassName   : CommonSecurityUtil.java
 * @Description : 스크립트 공격 취약성 방지(파라미터 문자열 교체)를 위한 Util Class
 * @author ADM기술팀
 * @since 2016. 6. 29.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 6. 29.     ADM기술팀     	최초 생성
 * </pre>
 */
public class CommonSecurityUtil {

	//select, delete, update, insert 등 기존 명령어와 알파벳, 숫자를 제외한 다른 문자들
	//(인젝션에 사용되는 특수문자 포함)을 검출하는 정규식을 설정한다.
	private final static String UNSECURED_CHAR_REGULAR_EXPRESSION =
		"[^\\p{Alnum}]|select|delete|update|insert|create|alter|drop";

	private final static String UNSECURED_CHAR_REGULAR_EXPRESSION_LDAP =
		"[^\\p{Alnum}]";

	private final static String CHAR_REGULAR_EXPRESSION = "[a-zA-Z]";   // 대소문자 알파벳

	private final static String SPECIALCHAR_REGULAR_EXPRESSION = "[^\\p{Alnum}]";   // 대소문자 알파벳, 숫자제외하고

	private final static String NUMBER_REGULAR_EXPRESSION = "[0-9]";    //숫자

	//정규식을 초기화 한다.
    /*public void initialize(){
        unsecuredCharPattern = Pattern.compile(UNSECURED_CHAR_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE);
    }*/

	//입력값을 정규식을 이용하여 필터링 후 의심되는 부분을 없앤다.
	public static String makeSecureString(final String str, int maxLength) {
		//정규식 초기화 (매번 이부분을 넣어야 하는지 고민해봐야..)
		Pattern unsecuredCharPattern = Pattern.compile(UNSECURED_CHAR_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE);

		String secureStr = str.substring(0, maxLength);
		Matcher matcher = unsecuredCharPattern.matcher(secureStr);
		return matcher.replaceAll("");
	}

	//행안부에서 정의하는 XSS 최소한 적용
	public static String clearXSSMinimum(String value) {
		if (value == null || value.trim().equals("")) {
			return "";
		}

		String returnValue = value;

		returnValue = returnValue.replaceAll("&", "&amp;");
		returnValue = returnValue.replaceAll("<", "&lt;");
		returnValue = returnValue.replaceAll(">", "&gt;");
		returnValue = returnValue.replaceAll("\"", "&#34;");
		returnValue = returnValue.replaceAll("\'", "&#39;");

		//아래 주석은 프로젝트에 따라 삭제 가능하다.
		//returnValue = returnValue.replaceAll("/","&#x2F;");
		//returnValue = returnValue.replaceAll("(","&#40;");
		//returnValue = returnValue.replaceAll(")","&#41;");
		//returnValue = returnValue.replaceAll(".", "&#46;");
		//returnValue = returnValue.replaceAll("%2E", "&#46;");
		//returnValue = returnValue.replaceAll("%2F", "&#47;");
		return returnValue;
	}

	//행안부에서 정의하는 XSS 최대한 적용
	public static String clearXSSMaximum(String value) {
		String returnValue = value;
		returnValue = clearXSSMinimum(returnValue);

		returnValue = returnValue.replaceAll("%00", null);

		returnValue = returnValue.replaceAll("%", "&#37;");

		// \\. => .

		returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
		returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\
		returnValue = returnValue.replaceAll("\\./", ""); // ./
		returnValue = returnValue.replaceAll("%2F", "");

		return returnValue;
	}

	public static String filePathBlackList(String value) {
		String returnValue = value;
		if (returnValue == null || returnValue.trim().equals("")) {
			return "";
		}

		returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
		returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\

		return returnValue;
	}

	/**
	 * 행안부 보안취약점 점검 조치 방안.
	 *
	 * @param value
	 * @return
	 */
	public static String filePathReplaceAll(String value) {
		String returnValue = value;
		if (returnValue == null || returnValue.trim().equals("")) {
			return "";
		}

		returnValue = returnValue.replaceAll("/", "");
		returnValue = returnValue.replaceAll("\\", "");
		returnValue = returnValue.replaceAll("\\.\\.", ""); // ..
		returnValue = returnValue.replaceAll("&", "");

		return returnValue;
	}

	//LDAP 명령어 인자값에서 특수문자 제외
	public static String makeSecureStringLDAP(final String str) {
		//정규식 초기화 (매번 이부분을 넣어야 하는지 고민해봐야..)
		Pattern unsecuredCharPattern = Pattern.compile(UNSECURED_CHAR_REGULAR_EXPRESSION_LDAP,
			Pattern.CASE_INSENSITIVE);

		Matcher matcher = unsecuredCharPattern.matcher(str);
		return matcher.replaceAll("");
	}

	public static String filePathWhiteList(String value) {
		return value;
	}

	public static boolean isIPAddress(String str) {
		Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

		return ipPattern.matcher(str).matches();
	}

	public static String removeCRLF(String parameter) {
		return parameter.replaceAll("\r", "").replaceAll("\n", "");
	}

	public static String removeSQLInjectionRisk(String parameter) {
		return parameter.replaceAll("\\p{Space}", "")
			.replaceAll("\\*", "")
			.replaceAll("%", "")
			.replaceAll(";", "")
			.replaceAll("-", "")
			.replaceAll("\\+", "")
			.replaceAll(",", "");
	}

	public static String removeOSCmdRisk(String parameter) {
		return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("|", "").replaceAll(";", "");
	}

	/**
	 * 비밀번호 입력시 강한 조건 검증 수행
	 * Statements
	 *
	 * @param str
	 * @return
	 */
	public static boolean passwordValidation(String str) {
		boolean result = true;

		if (null != str && str.trim().length() < 9)
			return false;

		Pattern expression1 = Pattern.compile(NUMBER_REGULAR_EXPRESSION);
		Matcher matcher1 = expression1.matcher(str);

		Pattern expression2 = Pattern.compile(SPECIALCHAR_REGULAR_EXPRESSION);
		Matcher matcher2 = expression2.matcher(str);

		Pattern expression3 = Pattern.compile(CHAR_REGULAR_EXPRESSION);
		Matcher matcher3 = expression3.matcher(str);

		if (!matcher1.find()) {
			//System.out.println(str+"=matcher1");
			return false;
		}
		if (!matcher2.find()) {
			//System.out.println(str+"=matcher2");
			return false;
		}
		if (!matcher3.find()) {
			//System.out.println(str+"=matcher3");
			return false;
		}

		return result;
	}
     
     /*
     public static String esapiEncode(String value){
        
         String safeEID = ESAPI.encoder().encodeForHTMLAttribute(value);
         
         return safeEID;
     }*/
}