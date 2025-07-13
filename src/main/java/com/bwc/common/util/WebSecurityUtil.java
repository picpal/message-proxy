package com.bwc.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSecurityUtil {

	public final static String UPLOAD_EXT = "uploadExt";
	public final static String UPLOAD_DETOUR = "uploadDetour";
	public final static String DOWNLOAD_EXT = "downloadExt";
	private final static String uploadRegExp = "([^\\s]+(\\.(?i)(asp|aspx|asa|cdx|cer|htr|jspx|jsw|jsv|jspf|jar|java|war|cgi|exe|inc|phtml|php|php3|php4|php5|hta|htx|mhtm|mhtml|mht|shtml|chm)){1,})";
	private final static String uploadDetour = "(\\%00|\\%ZZ|\\%0a|\\%2E|\\%2B|\\%5C|\\%20|\\%22|\\%70|\\%3f|#|\\:\\:){1,}";
	private final static String downloadRegExp = "(\\.\\.\\\\|\\.\\.\\/|\\.\\.\\.\\.\\/\\/|\\.\\.\\.\\/\\.\\/){1,}";
	private final static String xssRegExp = "(?i)('|\"|:|;|\\(|\\)|<|>|\\[|\\]|\\{|\\}|`|=|#|\\$|%|&|\\?|\\/|!|@|\\*|\t|\\||%27|%22|%3a|%3b|%28|%29|%3c|%3e|%5b|%5d|%7b|%7d|%60|%3d|%23|%24|%25|%26|%3f|%21|%40|%2a|%09|%7c|&#x|27;|&#x22;|&#x3a;|&#x3b;|&#x28;|&#x29;|&#x3c;|&#x3e;|&#x5b;|&#x5d;|&#x7b;|&#x7d;|&#x60;|&#x3d;|&#x23;|&#x24;|&#x25;|&#x26;|&#x3f;|&#x21;|&#x40;|&#x2a;|&#x09;|&#x7c;|script|javascript|vbscript|livescript|iframe|mocha|applet|img|embed|object|marquee|qss|body|input|form|div|style|table|isindex|meta|http-equiv|xss|href){1,}";
	private final static String boXssRegExp = "(?i)(%27|%22|%3a|%3b|%28|%29|%3c|%3e|%5b|%5d|%7b|%7d|%60|%3d|%23|%24|%25|%26|%3f|%21|%40|%2a|%09|%7c|&#x|27;|&#x22;|&#x3a;|&#x3b;|&#x28;|&#x29;|&#x3c;|&#x3e;|&#x5b;|&#x5d;|&#x7b;|&#x7d;|&#x60;|&#x3d;|&#x23;|&#x24;|&#x25;|&#x26;|&#x3f;|&#x21;|&#x40;|&#x2a;|&#x09;|&#x7c;|script|javascript|vbscript|livescript|iframe|mocha|applet|img|embed|object|marquee|qss|input|form|isindex|meta|http-equiv|xss|href|textarea|alert|confirm|frame|link|frameset|onclick|onkeyup|ondblclick|onplaying|onmousedown|oncanplay|onmouseup|onblur|onmouseover|onfocus|onmouseout|onscroll|onsubmit|onresize|onkeypress|onload|onunload|onabort|onerror|onselect|onchange|onreset){1,}";
	private final static String sqlInjcRegExp = "(?i)('|\"|:|;|\\(|\\)|<|>|\\[|\\]|\\{|\\}|`|=|#|\\$|%|&|\\?|!|@|\\*|\t|\\||%27|%22|%3a|%3b|%28|%29|%3c|%3e|%5b|%5d|%7b|%7d|%60|%3d|%23|%24|%25|%26|%3f|%21|%40|%2a|%09|%7c|&#x|27;|&#x22;|&#x3a;|&#x3b;|&#x28;|&#x29;|&#x3c;|&#x3e;|&#x5b;|&#x5d;|&#x7b;|&#x7d;|&#x60;|&#x3d;|&#x23;|&#x24;|&#x25;|&#x26;|&#x3f;|&#x21;|&#x40;|&#x2a;|&#x09;|&#x7c;|select|union|order by|where|join|create|drop|update|alter|from|and|or|asc|delay|return|instance|version|colnum|declare|then|if|else|end|exec|all|into|null|super|schema|case|case|desc|waitfor|table|having|banner|rownum|varchar|sleep\\(\\)|chr\\(\\)|ascii\\(\\)|substr\\(\\)|bitand\\(\\)|lower\\(\\)|concat\\(\\)|count\\(\\)|distinct\\(\\)|database\\(\\)|end\\(\\)|asciistr\\(\\)|instr\\(\\)|length\\(\\)|tochar\\(\\)){1,}";

	/**
	 * 파일 업로드 취약점 검사 : 업로드 파일 확장자 검사
	 *
	 * @param fileName
	 * @param param
	 * @return Map(result, 취약점 종류, 위반 문자)
	 */
	@SuppressWarnings("serial")
	public static HashMap<String, String> uploadFileExtCheck(String fileName, String param) {

		Pattern pattern = null;
		HashMap<String, String> result = new HashMap<String, String>() {
			{
				put("result", "false");
				put("securitySort", "none");
				put("violationChar", "none");
			}
		};

		if (param.equals("uploadExt")) {
			pattern = Pattern.compile(uploadRegExp);
		} else if (param.equals("uploadDetour")) {
			pattern = Pattern.compile(uploadDetour);
		}

		Matcher matcher = pattern.matcher(fileName);
		if (matcher != null && matcher.find()) {
			result.put("result", "true");
			result.put("securitySort", "FileUpload");
			result.put("violationChar", matcher.group());
		}
		return result;
	}

	/**
	 * XSS 취약 문자 변환
	 *
	 * @param values
	 * @return String[](변환된 parameters)
	 */
	public static String[] convertXSSParams(String[] values) {
		StringBuffer strBuff = new StringBuffer();

		for (int i = 0; i < values.length; i++) {
			if (values[i] != null) {
				for (int j = 0; j < values[i].length(); j++) {
					char c = values[i].charAt(j);
					switch (c) {
						case 39: // '`''
							strBuff.append("&#39;");
							break;
						case 34: // '"'
							strBuff.append("&quot;");
							break;
						case 58: // ':'
							strBuff.append("&#58;");
							break;
						case 59: // ';'
							strBuff.append("&#59;");
							break;
						case 40: // '('
							strBuff.append("&#40;");
							break;
						case 41: // ')'
							strBuff.append("&#41;");
							break;
						case 60: // '<'
							strBuff.append("&lt;");
							break;
						case 62: // '>'
							strBuff.append("&gt;");
							break;
						case 123: // '{'
							strBuff.append("&#123;");
							break;
						case 125: // '}'
							strBuff.append("&#125;");
							break;
						/*
						 * case 61 : // '='
						 * strBuff.append("&#61;");
						 * break;
						 */
						case 35: // '#'
							strBuff.append("&#35;");
							break;
						case 36: // '$'
							strBuff.append("&#36;");
							break;
						case 37: // '%'
							strBuff.append("&#37;");
							break;
						case 38: // '&'
							strBuff.append("&amp;");
							break;
						case 63: // '?'
							strBuff.append("&#63;");
							break;
						case 33: // '!'
							strBuff.append("&#33;");
							break;
						case 64: // '@'
							strBuff.append("&#64;");
							break;
						case 42: // '*'
							strBuff.append("&#42;");
							break;
						case 124: // '|'
							strBuff.append("&#124;");
							break;
						default:
							strBuff.append(c);
							break;
					}
				}
				values[i] = strBuff.toString();
				values[i] = reConvertXssString(values[i]);
				strBuff.setLength(0);
			} else {
				values[i] = null;
			}
		}
		return values;
	}

	/**
	 * XSS 취약 문자 변환(단건)
	 *
	 * @param value
	 * @return String (변환된 parameters)
	 */
	public static String convertXSSParam(String value) {

		StringBuffer strBuff = new StringBuffer();

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
				case 39: // '`''
					strBuff.append("&#39;");
					break;
				case 34: // '"'
					strBuff.append("&quot;");
					break;
				case 58: // ':'
					strBuff.append("&#58;");
					break;
				case 59: // ';'
					strBuff.append("&#59;");
					break;
				case 40: // '('
					strBuff.append("&#40;");
					break;
				case 41: // ')'
					strBuff.append("&#41;");
					break;
				case 60: // '<'
					strBuff.append("&lt;");
					break;
				case 62: // '>'
					strBuff.append("&gt;");
					break;
				case 123: // '{'
					strBuff.append("&#123;");
					break;
				case 125: // '}'
					strBuff.append("&#125;");
					break;
				/*
				 * case 61 : // '='
				 * strBuff.append("&#61;");
				 * break;
				 */
				case 35: // '#'
					strBuff.append("&#35;");
					break;
				case 36: // '$'
					strBuff.append("&#36;");
					break;
				case 37: // '%'
					strBuff.append("&#37;");
					break;
				case 38: // '&'
					strBuff.append("&amp;");
					break;
				case 63: // '?'
					strBuff.append("&#63;");
					break;
				case 33: // '!'
					strBuff.append("&#33;");
					break;
				case 64: // '@'
					strBuff.append("&#64;");
					break;
				case 42: // '*'
					strBuff.append("&#42;");
					break;
				case 124: // '|'
					strBuff.append("&#124;");
					break;
				default:
					strBuff.append(c);
					break;
			}
		}
		value = strBuff.toString();
		value = reConvertXssString(value);
		return value;
	}

	/**
	 * XSS 취약 문자 변환(단건)
	 *
	 * @param value
	 * @return String (변환된 parameters)
	 */
	public static String convertXSSJsonParam(String value) {

		StringBuffer strBuff = new StringBuffer();

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
				case 39: // '`''
					strBuff.append("&#39;");
					break;
                /*case 34: // '"'
                    strBuff.append("&quot;");
                    break;*/
                /*case 58: // ':'
                    strBuff.append("&#58;");
                    break;*/
				case 59: // ';'
					strBuff.append("&#59;");
					break;
				case 40: // '('
					strBuff.append("&#40;");
					break;
				case 41: // ')'
					strBuff.append("&#41;");
					break;
				case 60: // '<'
					strBuff.append("&lt;");
					break;
				case 62: // '>'
					strBuff.append("&gt;");
					break;
                /*case 123: // '{'
                    strBuff.append("&#123;");
                    break;
                case 125: // '}'
                    strBuff.append("&#125;");
                    break;*/
				/*
				 * case 61 : // '='
				 * strBuff.append("&#61;");
				 * break;
				 */
				case 35: // '#'
					strBuff.append("&#35;");
					break;
				case 36: // '$'
					strBuff.append("&#36;");
					break;
				case 37: // '%'
					strBuff.append("&#37;");
					break;
				case 38: // '&'
					strBuff.append("&amp;");
					break;
				case 63: // '?'
					strBuff.append("&#63;");
					break;
				case 33: // '!'
					strBuff.append("&#33;");
					break;
				case 64: // '@'
					strBuff.append("&#64;");
					break;
				case 42: // '*'
					strBuff.append("&#42;");
					break;
				case 124: // '|'
					strBuff.append("&#124;");
					break;
				default:
					strBuff.append(c);
					break;
			}
		}
		value = strBuff.toString();
		value = reConvertXssString(value);
		return value;
	}

	/**
	 * XSS 취약 문자 변환 Data 재취환
	 *
	 * @param checkValue
	 * @return value
	 */
	public static String reConvertXssString(String checkValue) {
		String value = checkValue;

		if (value.indexOf("&amp;&#35;39&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;39&#59;", "&#39;");
		}
		if (value.indexOf("&amp;quot&#59;") > -1) {
			value = value.replaceAll("&amp;quot&#59;", "&quot;");
		}
		if (value.indexOf("&amp;&#35;58&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;58&#59;", "&#58;");
		}
		if (value.indexOf("&amp;&#35;59&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;59&#59;", "&#59;");
		}
		if (value.indexOf("&amp;&#35;40&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;40&#59;", "&#40;");
		}
		if (value.indexOf("&amp;&#35;41&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;41&#59;", "&#41;");
		}
		if (value.indexOf("&amp;lt&#59;") > -1) {
			value = value.replaceAll("&amp;lt&#59;", "&lt;");
		}
		if (value.indexOf("&amp;gt&#59;") > -1) {
			value = value.replaceAll("&amp;gt&#59;", "&gt;");
		}
		if (value.indexOf("&amp;&#35;123&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;123&#59;", "&#123;");
		}
		if (value.indexOf("&amp;&#35;125&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;125&#59;", "&#125;");
		}
		if (value.indexOf("&amp;&#35;35&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;35&#59;", "&#35;");
		}
		if (value.indexOf("&amp;&#35;36&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;36&#59;", "&#36;");
		}
		if (value.indexOf("&amp;&#35;37&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;37&#59;", "&#37;");
		}
		if (value.indexOf("&amp;amp&#59;") > -1) {
			value = value.replaceAll("&amp;amp&#59;", "&amp;");
		}
		if (value.indexOf("&amp;&#35;63&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;63&#59;", "&#63;");
		}
		if (value.indexOf("&amp;&#35;33&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;33&#59;", "&#33;");
		}
		if (value.indexOf("&amp;&#35;64&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;64&#59;", "&#64;");
		}
		if (value.indexOf("&amp;&#35;42&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;42&#59;", "&#42;");
		}
		if (value.indexOf("&amp;&#35;124&#59;") > -1) {
			value = value.replaceAll("&amp;&#35;124&#59;", "&#124;");
		}

		return value;
	}

	/**
	 * XSS 취약 문자 검사
	 *
	 * @param values
	 * @return Map(result, 취약점 종류, 위반 문자)
	 */
	@SuppressWarnings("serial")
	public static HashMap<String, String> checkXSSParams(String values) {
		HashMap<String, String> result = new HashMap<String, String>() {
			{
				put("result", "false");
				put("securitySort", "none");
				put("violationChar", "none");
			}
		};

		Pattern pattern = Pattern.compile(xssRegExp);
		Matcher matcher;
		matcher = pattern.matcher(values);

		if (matcher.find()) {
			result.put("result", "true");
			result.put("securitySort", "XSS");
			result.put("violationChar", matcher.group());
		}

		return result;
	}

	/**
	 * XSS 취약 문자 검사
	 *
	 * @param values
	 * @return Map(result, 취약점 종류, 위반 문자)
	 */
	@SuppressWarnings("serial")
	public static HashMap<String, String> checkBoXSSParams(String values) {
		HashMap<String, String> result = new HashMap<String, String>() {
			{
				put("result", "false");
				put("securitySort", "none");
				put("violationChar", "none");
			}
		};

		Pattern pattern = Pattern.compile(boXssRegExp);
		Matcher matcher;
		matcher = pattern.matcher(values);

		if (matcher.find()) {
			result.put("result", "true");
			result.put("securitySort", "XSS");
			result.put("violationChar", matcher.group());
		}

		return result;
	}

	/**
	 * SQL Injection 취약 문자 검사
	 *
	 * @param values
	 * @return Map(result, 취약점 종류, 위반 문자)
	 */
	@SuppressWarnings("serial")
	public static HashMap<String, String> checkSQLInjectionParams(String values) {
		HashMap<String, String> result = new HashMap<String, String>() {
			{
				put("result", "false");
				put("securitySort", "none");
				put("violationChar", "none");
			}
		};

		Pattern pattern = Pattern.compile(sqlInjcRegExp);
		Matcher matcher;
		matcher = pattern.matcher(values);

		if (matcher.find()) {
			result.put("result", "true");
			result.put("securitySort", "SQL Injection");
			result.put("violationChar", matcher.group());
		}

		return result;
	}

	/**
	 * 다운로드 취약점 검사
	 *
	 * @param values
	 * @return true/false
	 */
	@SuppressWarnings("serial")
	public static HashMap<String, String> checkDownloadParams(String values) throws UnsupportedEncodingException {

		Pattern checkPattern = Pattern.compile("(%\\p{Alnum}{1}\\p{Alnum}{1}){1,}");
		Matcher m;
		m = checkPattern.matcher(values);
		if (m.find()) {
			values = URLDecoder.decode(values, "utf-8");
			values = URLDecoder.decode(values, "utf-8");
		}

		HashMap<String, String> result = new HashMap<String, String>() {
			{
				put("result", "false");
				put("securitySort", "none");
				put("violationChar", "none");
			}
		};

		Pattern pattern = Pattern.compile(downloadRegExp);
		Matcher matcher;
		matcher = pattern.matcher(values);

		if (matcher.find()) {
			result.put("result", "true");
			result.put("securitySort", "File Download");
			result.put("violationChar", matcher.group());
		}

		return result;
	}

}