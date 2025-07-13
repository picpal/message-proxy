package com.bwc.common.util;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 고유번호생성유틸
 * </pre>
 *
 * @ClassName   : NtnoCrtnUtils.java
 * @Description : 고유번호를 생성한다.
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

@Component
public class NtnoCrtnUtils {

	/** PropertyService */
	@Value("${trx.guid.prifix}")
	private String trxGuidPrifix;

	/**
	 *
	 * 거래GUID 생성
	 *
	 * @return
	 */
	public String occrTrxGuid() {
		//String trxGuidPrifix = trxGuidPrifix;

		return trxGuidPrifix + systemPrefix() + DateUtils.getMillisecTime() + occrRmno(2) + occrSqnc(2);
	}

	/**
	 * 세션GUID 생성
	 * Statements
	 *
	 * @return
	 */
	public String occrSessionGuid() {
		//String trxGuidPrifix = propertyService.getString("trx.guid.prifix");
		return trxGuidPrifix + systemPrefix() + "SG" + DateUtil.getCurrentDateTime("yyMMddHHmmssSSS") + occrRmno(2)
			+ occrSqnc3(2);
	}

	/**
	 *
	 * 시스템서버구분 반환
	 *
	 * @return
	 */
	public static String systemPrefix() {

		/*
		 * 서버명 keboXX_01, keboXX_02...
		 * 컨테이너명 kebo01, kebo02 ...
		 * 반환값 11, 12, 21, 22....
		 */
		StringBuffer sb = new StringBuffer();
		if (System.getProperties().toString().indexOf("tkbo01") != -1) {
			sb.append("2");
		} else {
			sb.append("1");
		}

		if (System.getProperties().toString().indexOf("tkbo02") != -1) {
			sb.append("2");
		} else {
			sb.append("1");
		}

		return sb.toString();
	}

	/**
	 *
	 * 해당자릿수의 난수발생
	 * 예) occrRmno(3) 이면 "123" 반환, occrRmno(5) "00218"
	 *
	 * @param i
	 * @return
	 */
	public static String occrRmno(int i) {
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

	/*trx_guid 용*/
	public static int sqnc = 0;

	public static synchronized String occrSqnc(int i) {
		if (sqnc > 90) {
			sqnc = 0;
		} else {
			sqnc++;
		}
		return StrUtil.leftPad(String.valueOf(sqnc), i, "0");
	}

	/*내부대체식별번호 용*/
	public static int sqnc2 = 0;

	public static synchronized String occrSqnc2(int i) {
		if (sqnc2 > 90) {
			sqnc2 = 0;
		} else {
			sqnc2++;
		}
		return StrUtil.leftPad(String.valueOf(sqnc2), i, "0");
	}

	/*세션GUID 용*/
	public static int sqnc3 = 0;

	public static synchronized String occrSqnc3(int i) {
		if (sqnc3 > 90) {
			sqnc3 = 0;
		} else {
			sqnc3++;
		}
		return StrUtil.leftPad(String.valueOf(sqnc3), i, "0");
	}

	/**
	 *
	 * 내부대체식별번호추출
	 * A+C/B/T + YYYYMMDDHH25MIssSSS+난수(3) + 시퀀스(2) = 24자리
	 * @param innrTrfrIdnoTp 내부대체식별번호추출구분
	 * @return
	 */
	public String getInnrTrfrIdno(String innrTrfrIdnoTp) {
		return innrTrfrIdnoTp + DateUtils.getMillisecTime() + occrRmno(3) + occrSqnc2(2);
	}
}
