package com.bwc.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : DateUtils.java
 * @Description : 클래스 설명을 기술합니다.
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

public class DateUtils {

	/**
	 * 현재 년월 - YYYYMM
	 */
	public static String getMonth() {
		return getDate().substring(0, 6);
	}

	/**
	 * 현재 년월일 - YYYYMMDD
	 */
	public static String getDate() {

		String month, day;

		Calendar cal = Calendar.getInstance(Locale.getDefault());

		StringBuffer buf = new StringBuffer();
		buf.append(Integer.toString(cal.get(Calendar.YEAR)));

		month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		if (month.length() == 1)
			month = "0" + month;

		day = Integer.toString(cal.get(Calendar.DATE));
		if (day.length() == 1)
			day = "0" + day;

		buf.append(month);
		buf.append(day);

		return buf.toString();
	}

	/**
	 * 현재 시간 - HHMISS
	 */
	public static String getTime() {

		String hour, min, sec;

		Calendar cal = Calendar.getInstance(Locale.getDefault());

		StringBuffer buf = new StringBuffer();

		hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		if (hour.length() == 1)
			hour = "0" + hour;

		min = Integer.toString(cal.get(Calendar.MINUTE));
		if (min.length() == 1)
			min = "0" + min;

		sec = Integer.toString(cal.get(Calendar.SECOND));
		if (sec.length() == 1)
			sec = "0" + sec;

		buf.append(hour);
		buf.append(min);
		buf.append(sec);

		return buf.toString();
	}

	/**
	 *
	 * 초단위시간 조회
	 *
	 * @return
	 */
	public static String getSecTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		return sdf.format(calendar.getTime());
	}

	/**
	 *
	 * 밀리세컨드초단위시간 조회
	 *
	 * @return
	 */
	public static String getMillisecTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		return sdf.format(calendar.getTime());
	}

	/**
	 *
	 * 특정날짜에 일자나 월수 더한 값 반환
	 *
	 * @param dateTime
	 * @param plus 더할 수
	 * @param isDay 일자인지여부
	 * @return
	 */
	public static String getDateCalculation(String dateTime, int plus, boolean isDay) {

		if (dateTime == null)
			return "";

		if (dateTime.startsWith("99991231")) {
			return dateTime;
		}

		String hms = "";
		if (dateTime.length() > 8)
			hms = dateTime.substring(8);

		int y = Integer.parseInt(dateTime.substring(0, 4));
		int m = Integer.parseInt(dateTime.substring(4, 6));
		int d = Integer.parseInt(dateTime.substring(6, 8));

		GregorianCalendar today = new GregorianCalendar();
		today.set(y, m - 1, d);
		if (isDay)
			today.add(GregorianCalendar.DAY_OF_MONTH, plus);
		else
			today.add(GregorianCalendar.MONTH, plus);

		int day = today.get(GregorianCalendar.DAY_OF_MONTH);
		int month = today.get(GregorianCalendar.MONTH) + 1;
		int year = today.get(GregorianCalendar.YEAR);

		StringBuffer buf = new StringBuffer();
		buf.append(String.valueOf(year));

		if (month < 10)
			buf.append("0");
		buf.append(String.valueOf(month));

		if (day < 10)
			buf.append("0");
		buf.append(String.valueOf(day));

		buf.append(hms);

		return buf.toString();
	}

	/**
	 *
	 * 특정날짜에 일자를 더한 값 반환
	 *
	 * @param dateTime YYYYMMDDHHMMSS or YYYYMMDD
	 * @param plusDay 더할 일자
	 * @return
	 */
	public static String getAddDay(String date, int plusDay) {
		return getDateCalculation(date, plusDay, true);
	}

	/**
	 *
	 * 특정날짜에 달을 더한 값 반환
	 *
	 * @param dateTime YYYYMMDDHHMMSS or YYYYMMDD
	 * @param plusMonth 더한 달 수
	 * @return
	 */
	public static String getAddMonth(String date, int plusMonth) {
		return getDateCalculation(date, plusMonth, false);
	}

	/**
	 * 어제일자 - YYYYMMDD
	 */
	public static String getYesterday() {
		return getAddDay(getDate(), -1);
	}

	/**
	 * 내일일자 - YYYYMMDD
	 */
	public static String getTomorrow() {
		return getAddDay(getDate(), 1);
	}

	/**
	 *
	 * 일자 유효성 체크
	 *
	 * @param str 검사 문자열
	 * @return
	 */
	public static boolean isDate(String str) {
		if (str == null || str.length() != 8)
			return false;

		try {
			int year = Integer.parseInt(str.substring(0, 4));
			int month = Integer.parseInt(str.substring(4, 6));
			int day = Integer.parseInt(str.substring(6, 8));

			if (year < 1900 || year > 9999 || month < 1 || month > 12 || day < 1 || day > 31) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getDateFormat(Date date, String format) {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
		return sdf.format(date);
	}

	// YYYY-MM-DD 형태로 반환
	public static String getStringToDateForm(String strDt) {

		if (strDt == null || strDt.length() != 8)
			return "";

		String year = strDt.substring(0, 4);
		String month = strDt.substring(4, 6);
		String day = strDt.substring(6, 8);

		String resultDt = year + "-" + month + "-" + day;

		return resultDt;
	}

	/**
	 *
	 * YYYYMMDDHH24MISS 유효성 체크
	 *
	 * @param str 검사 문자열
	 * @return
	 */
	public static boolean isVaildDate(String str) {
		if (str == null || str.length() != 14)
			return false;

		try {
			int year = Integer.parseInt(str.substring(0, 4));
			int month = Integer.parseInt(str.substring(4, 6));
			int day = Integer.parseInt(str.substring(6, 8));
			int hour = Integer.parseInt(str.substring(8, 10));
			int min = Integer.parseInt(str.substring(10, 12));
			int sec = Integer.parseInt(str.substring(12, 14));

			if (year < 1900 || year > 9999 || month < 1 || month > 12 || day < 1 || day > 31 || hour < 0 || hour > 23
				|| min < 0 || min > 59 || sec < 0 || sec > 59) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 *
	 * 시간 유효성 체크 (HH24MISS)
	 *
	 * @param str 검사 문자열
	 * @return
	 */
	public static boolean isTime(String str) {
		if (str == null || str.length() != 6)
			return false;

		try {
			int hour = Integer.parseInt(str.substring(0, 2));
			int min = Integer.parseInt(str.substring(2, 4));
			int sec = Integer.parseInt(str.substring(4, 6));

			if (hour < 0 || hour > 23 || min < 0 || min > 59 || sec < 0 || sec > 59) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
