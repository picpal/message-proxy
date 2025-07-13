package com.bwc.common.util;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DecimalFormat;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : StrUtil.java
 * @Description : 클래스 설명을 기술합니다.
 * @author SY.LEE
 * @since 2018. 2. 8.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018. 2. 8.     SY.LEE      최초 생성
 * </pre>
 */

@Component("strUtil")
public class StrUtil {

	public static final String EMPTY = "";

	/**
	 * 휴대폰번호 Mask
	 *
	 */
	@SuppressWarnings("unused")
	public static String maskMbphNo(String mbphNo) {
		String strMbphNo = nullToStr(mbphNo);
		String maskString = nullToStr(strMbphNo);

		Matcher matcher = Pattern.compile("^(\\d{3})-?(\\d{3,4})-?(\\d{4})$").matcher(strMbphNo);

		if (matcher.matches()) {
			maskString = "";

			boolean isHyphen = false;

			if (strMbphNo.indexOf("-") > -1) {
				isHyphen = true;
			}

			for (int i = 1; i <= matcher.groupCount(); i++) {

				String maskTarget = matcher.group(i);

				if (i == 2) {
					char[] c = new char[maskTarget.length()];
					String s = subStr(maskTarget, 2);
					Arrays.fill(c, 2, c.length, '*');

					String tmpEcpyRcprNo = s + String.valueOf(c);
					maskString = maskString + zeroIgnore(tmpEcpyRcprNo);
				} else if (i == 3) {
					char[] c = new char[maskTarget.length()];
					String s = subStr(maskTarget, 2);
					Arrays.fill(c, 2, c.length, '*');

					String tmpEcpyRcprNo = s + String.valueOf(c);
					maskString = maskString + zeroIgnore(tmpEcpyRcprNo);
				} else {
					maskString = maskString + maskTarget;
				}
				//                if (isHyphen && i < matcher.groupCount()) {
				if (i < matcher.groupCount()) {
					maskString = maskString + "-";
				}
			}

		} else {
			maskString = strMbphNo;
		}

		return maskString;
	}

	/**
	 * 계좌번호 마스킹...
	 * Statements
	 *
	 * @param acNo
	 * @return
	 */
	public static String makeAcno(String acNo) {
		if (acNo == null || acNo.equals("")) {
			return "";
		}

		if (acNo.length() < 5) {
			return acNo;
		}

		acNo = acNo.replaceAll("-", "");

		String first = acNo.substring(0, 3);
		String end = acNo.substring(acNo.length() - 1, acNo.length());

		String middle = acNo.substring(3, acNo.length() - 1);

		String str = "";
		for (int i = 0; i < middle.length(); i++) {
			str += "*";
		}

		return first + str + end;

	}

	// 제공되지 않은 공백문자 삭제
	public static String zeroIgnore(String tmpEcpyRcprNo) {
		String strNo = "";
		for (int j = 0; j < tmpEcpyRcprNo.length(); j++) {
			int unicodeCharData = (int)tmpEcpyRcprNo.charAt(j);
			if (tmpEcpyRcprNo.charAt(j) != ' ' && unicodeCharData != 0) {
				strNo += tmpEcpyRcprNo.charAt(j);
			}
		}
		return strNo;
	}

	/**
	 * 차대번호 Mask
	 *
	 */

	public static String maskCbNo(String cbno) {

		if (cbno == null || "".equals(cbno)) {
			return "";
		}

		String fNo = cbno.substring(0, 3);
		String lNo = cbno.substring(9, cbno.length());

		cbno = fNo + "******" + lNo;

		return cbno;
	}

	/**
	 * 이름 Mask
	 *
	 */
	public static String maskMbNm(String name) {

		if (name == null || "".equals(name)) {
			return "";
		}

		String fName = name.substring(0, 1);
		String lName = name.substring(1, name.length());

		if (lName.length() == 2) {
			lName = "*" + lName.substring(1, lName.length());
		} else if (lName.length() > 2) {
			lName = "**" + lName.substring(2, lName.length());
		} else {
			lName = "*";
		}

		name = fName + lName;
		;

		return name;
	}

	/**
	 * 이메일주소 Mask
	 *
	 */
	public static String maskEmad(String emad) {
		String strEmad = nullToStr(emad);
		String maskString = nullToStr(strEmad);
		String[] email = strEmad.split("\\@");
		String pattern = "";

		if (email.length == 2) {
			if (email[0].length() > 2) {
				pattern = "^(.*)(..)([@]{1})(.*)$";
			} else {
				pattern = "^(.*)(.)([@]{1})(.*)$";
			}

			Matcher matcher = Pattern.compile(pattern).matcher(strEmad);

			if (matcher.matches()) {
				maskString = "";

				for (int i = 1; i <= matcher.groupCount(); i++) {

					String maskTarget = matcher.group(i);

					if (i == 2) {
						char[] c = new char[maskTarget.length()];
						Arrays.fill(c, '*');

						maskString = maskString + String.valueOf(c);
					} else {
						maskString = maskString + maskTarget;
					}
				}
			}
		} else {
			maskString = strEmad;
		}

		return maskString;
	}

	/**
	 *
	 * 문자열을 오른쪽부터 길이만큼 자르기
	 *
	 * @param str
	 * @param length
	 * @return
	 */
	public static String subStr(String str, int length) {
		if (str.length() < length) {
			throw new ArrayIndexOutOfBoundsException(length);
		}
		String tmp = "";
		for (int i = 0; i < length; i++) {
			tmp = tmp + str.charAt(i);
		}
		return tmp;
	}

	/**
	 *
	 * 문자열을 길이만큼 왼쪽으로 Char 채워서 반환
	 *
	 * @param str
	 * @param length
	 * @param ch
	 * @return
	 */
	public static String leftPad(String str, int length, String ch) {
		String tmp = str;
		if (tmp == null)
			tmp = "";
		while (tmp.length() < length) {
			tmp = ch + tmp;
		}
		return tmp;
	}

	/**
	 *
	 * 문자열을 길이만큼 오른쪽에 Char 채워서 반환
	 *
	 * @param str
	 * @param length
	 * @param ch
	 * @return
	 */
	public static String rightPad(String str, int length, String ch) {
		String tmp = str;
		if (tmp == null)
			tmp = "";
		while (tmp.length() < length) {
			tmp = tmp + ch;
		}
		return tmp;
	}

	/**
	 *
	 * 문자열을 길이만큼 왼쪽에 0을 채워서 반환
	 *
	 * @param str
	 * @param length
	 * @param ch
	 * @return
	 */
	public static String fillZero(String str, int length) {
		String tmp = str;
		if (tmp == null)
			tmp = "";
		while (tmp.length() < length) {
			tmp = "0" + tmp;
		}
		return tmp;
	}

	/**
	 * 숫자 천단위 콤마(,) 찍기
	 *
	 */
	public static String setComma(int num) {

		DecimalFormat Comma = new DecimalFormat("#,###");

		String retrun = (String)Comma.format(num);

		return retrun;
	}

	/**
	 *
	 * 만원, 천원 단위로 변경
	 * @param amt 금액
	 * @returns 만원, 천원 단위로 변경된 금액
	 *
	 */
	public static String toMoneyKor(String amt) {
		if (amt.equals(""))
			return "0원";

		String rtnAmt = "";

		int temp1 = Integer.parseInt(amt);

		if (temp1 % 10000 != 0) {
			rtnAmt = (int)Math.floor(temp1 / 10000) + "만" + (int)Math.floor((temp1 % 10000) / 1000) + "천원";
		} else {
			rtnAmt = (int)temp1 / 10000 + "만원";
		}

		return rtnAmt;
	}

	/**
	 * String null 체크..
	 * Statements
	 *
	 * @param str
	 * @return
	 */
	public static String isNull(String str) {
		if (str == null) {
			return "";
		}
		if ("null".equals(str) || "NULL".equals(str)) {
			return "";
		}

		if (str.length() == 0) {
			return "";
		}

		return str;
	}

	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		if ("null".equals(str) || "NULL".equals(str)) {
			return true;
		}

		if (str.length() == 0) {
			return true;
		}

		return false;
	}

	public static String getError(Exception e) {
		StringWriter errors = new StringWriter();
		/*
		 * [CWE-209] CodeRay 취약점
		 * 주석 처리
		 * */
		// e.printStackTrace(new PrintWriter(errors));

		return errors.toString();
	}

	/**
	 *
	 * String 객체가 Null 인경우 "" 반환한다.
	 *
	 * @param str
	 * @return
	 */
	public static String nullToStr(String str) {
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}

	public static String nullToStr(Object object) {
		String string = "";
		if (object != null) {
			string = object.toString().trim();
		}

		return string;
	}

	/**
	 * 휴대폰 번호 양식에 맞춰 변경 및 마스킹 진행
	 *
	 * @param num
	 * @param mask
	 * @return
	 */
	public static String phoneNumberHyphenAdd(String num, String mask) {

		String formatNum = "";
		if (isEmpty(num)) {
			return formatNum;
		}

		num = num.replaceAll("-", "");

		if (num.length() == 11) {
			if (mask.equals("Y")) {
				formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
			} else {
				formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
			}

		} else if (num.length() == 10) {
			if (mask.equals("Y")) {
				formatNum = num.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-***-$3");
			} else {
				formatNum = num.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
			}

		} else if (num.length() == 8) {
			formatNum = num.replaceAll("(\\d{4})(\\d{4})", "$1-$2");

		} else {

			if (num.indexOf("02") == 0) {
				if (mask.equals("Y")) {
					formatNum = num.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-****-$3");
				} else {
					formatNum = num.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-$2-$3");
				}
			} else {
				if (mask.equals("Y")) {
					formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
				} else {
					formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
				}
			}
		}
		return formatNum;
	}

	/**
	 * 넘겨받은 숫자를 소수점 올림처리하여 리턴한다.
	 *
	 * @param num
	 * @param mask
	 * @return
	 */
	public static int getMathCeil(double number) {
		return (int)Math.ceil(number);
	}

	/**
	 * 문자열의 바이트 길이를 가져온다.
	 *
	 * @param num
	 * @param mask
	 * @return
	 */
	public static int getByteLength(String str) {
		if (str == null) {
			return 0;
		}

		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		return bytes.length;
	}

	/**
	 * 입력한 숫자만큼의 문자열 랜덤 숫자를 생성
	 *
	 * @param num
	 * @param mask
	 * @return
	 */
	public static String getRandomNumText(int i) {
		SecureRandom random = new SecureRandom();
		random.setSeed(new Date().getTime());

		int num = random.nextInt((int)Math.pow(10, i));
		String strN = String.valueOf(num);
		if (strN.length() > i) {
			strN = strN.substring(0, i);
		} else {
			strN = StrUtil.rightPad(strN, i, "0");
		}
		return strN;
	}

	/**
	 *
	 * 문자열의 NULL 혹은 빈문자열 여부를 반환한다.
	 *
	 * @param str 문자열
	 * @return
	 * NULL 혹은 빈문자열 일경우 true
	 * 아닐경우 false
	 */
	public static boolean isNVL(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 차량번호 Mask
	 * e.g) 123가1234 => 1**가****
	 *      12가1234  =>  1*가****
	 */
	public static String maskVhclNo(String vhclNo) {

		if (vhclNo == null || "".equals(vhclNo)) {
			return "";
		}
		String fNo = null;
		String lNo = null;

		if (vhclNo.length() == 8) {
			fNo = vhclNo.substring(0, 1);
			lNo = vhclNo.substring(3, 4);
			vhclNo = fNo + "**" + lNo + "****";
		} else if (vhclNo.length() == 7) {
			fNo = vhclNo.substring(0, 1);
			lNo = vhclNo.substring(2, 3);
			vhclNo = fNo + "*" + lNo + "****";
		} else {
			return "Wrong Plate number![" + vhclNo + "]";
		}

		return vhclNo;
	}

	/**
	 *승인 IP Mask
	 * e.g) 10.123.123.1 ==> 10.*.*.1
	 *
	 */

	public static String maskIp(String ip) {

		if (ip == null | "".equals(ip)) {
			return "";
		}

		String[] ipParts = ip.split("\\.");
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; ipParts.length > i; i++) {

			if (i == 1 || i == 2) {
				buffer.append("*");
			} else {
				buffer.append(ipParts[i]);
			}
			if (i < ipParts.length - 1) {
				buffer.append(".");
			}
		}
		return buffer.toString();
	}

	/**
	 * Map 데이터를 JsonString 데이터로 변환
	 * */
	public static String convertMapToJsonStr(Map map) {
		if (map == null) {
			return "";
		}

		JSONObject json = new JSONObject();
		json.putAll(map);
		return json.toJSONString();
	}

}