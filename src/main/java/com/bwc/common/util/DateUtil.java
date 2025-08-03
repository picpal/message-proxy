package com.bwc.common.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 프로젝트 전반에 사용되는 Date Util Class
 *
 * @ClassName DateUtil.java
 * @Description DateUtil Class
 * @Modification-Information
 * <pre>
 *    수정일       수정자              수정내용
 *  ----------   ----------   -------------------------------
 *  2020. 7. 1.    lucifer      최초생성
 * </pre>
 * @author lucifer
 * @since 2020. 7. 1.
 * @version 1.0
 * @see
 * <pre>
 *  Copyright (C) 2020 by Taihoinst CO.,LTD. All right reserved.
 * </pre>
 */
@Slf4j
public final class DateUtil {

	private DateUtil() {
	}

	/**
	 *  오라클DB의 DATE형식 객체 생성(현재 년월일시간분초)
	 *  @return Timestamp
	 */
	public static Timestamp getCurrentTimestamp() {
		Calendar calendar = Calendar.getInstance();

		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 *  현재 날짜를  yyyyMM 형태로 반환한다.
	 *  @return String
	 */
	public static String getYyyymm() {
		Calendar calendar = Calendar.getInstance();
		Locale currentLocale = new Locale("KOREAN", "KOREA");
		String pattern = "yyyyMM";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
		return formatter.format(calendar.getTime());
	}

	/**
	 *  현재 날짜와 시각을  yyyyMMdd 형태로 반환한다.
	 *  @return String
	 */
	public static String getYyyymmdd() {
		Calendar calendar = Calendar.getInstance();
		Locale currentLocale = new Locale("KOREAN", "KOREA");
		String pattern = "yyyyMMdd";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
		return formatter.format(calendar.getTime());
	}

	/**
	 * 현재 날짜와 시각을 Yyyymmddhhmmss 형태로 반환한다.
	 *
	 * @return
	 */
	public static String getYyyymmddhhmmss() {
		Calendar calendar = Calendar.getInstance();
		Locale currentLocale = new Locale("KOREAN", "KOREA");
		String pattern = "yyyyMMddHHmmss";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
		return formatter.format(calendar.getTime());
	}

	/**
	 * 현재 날짜와 시각을 Yyyymmdd 형태로 반환한다.
	 *
	 * @param cal
	 * @return
	 */
	public static String getYyyymmdd(
		Calendar cal) {
		Locale currentLocale = new Locale("KOREAN", "KOREA");
		String pattern = "yyyy.MM.dd";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
		return formatter.format(cal.getTime());
	}

	/**
	 * 현재 날짜와 시각을 인자의 pattern으로 변환한다.
	 *
	 * @param cal
	 * @return
	 */
	public static String getNowDate(
		String pattern) {
		Locale currentLocale = new Locale("KOREAN", "KOREA");
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
		return formatter.format(Calendar.getInstance().getTime());
	}

	/**
	 * getGregorianCalendar
	 *
	 * @param yyyymmdd
	 * @return
	 */
	public static GregorianCalendar getGregorianCalendar(
		String yyyymmdd) {
		int yyyy = Integer.parseInt(yyyymmdd.substring(0, 4));
		int mm = Integer.parseInt(yyyymmdd.substring(4, 6));
		int dd = Integer.parseInt(yyyymmdd.substring(6));

		GregorianCalendar calendar = new GregorianCalendar(yyyy, mm - 1, dd, 0, 0, 0);

		return calendar;
	}

	/**
	 *  지정된 플래그에 따라 연도 , 월 , 일자를 연산한다.
	 *  - 사용 예
	 * String date = DateUtil.getOpDate(java.util.Calendar.DATE , 1, "20080101")
	 *  @return String
	 */
	public static String getOpDate(
		int field,
		int amount,
		String date) {
		GregorianCalendar calDate = getGregorianCalendar(date);

		if (field == Calendar.YEAR) {
			calDate.add(GregorianCalendar.YEAR, amount);
		} else if (field == Calendar.MONTH) {
			calDate.add(GregorianCalendar.MONTH, amount);
		} else {
			calDate.add(GregorianCalendar.DATE, amount);
		}

		return getYyyymmdd(calDate);
	}

	/**
	 * 2009-03-10 String날짜변수를  2009-03-10 00:00:00 Timestamp 형식으로 반환한다.
	 *
	 * @param dateStr
	 * @return
	 */
	public static Timestamp replaceTimestamp(
		String dateStr) {
		if (dateStr == null || dateStr.length() != 10)
			return null;

		String year = dateStr.substring(0, 4);
		String month = dateStr.substring(5, 7);
		String day = dateStr.substring(8, 10);
		//		String hh    = dateStr.substring(11, 13);
		//		String mm    = dateStr.substring(14, 16);
		//		String ss    = dateStr.substring(17, 18);

		Calendar calendar = Calendar.getInstance();

		calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), 0, 0, 0);

		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * 20101013112322 String날짜변수를  2009.03.10 11:23:22 Timestamp 형식으로 반환한다.
	 *
	 * @param dateStr
	 * @return
	 */
	public static Timestamp replaceTimestampType1(
		String dateStr) {
		if (dateStr == null || dateStr.length() != 14)
			return null;

		String year = dateStr.substring(0, 4);
		String month = dateStr.substring(4, 6);
		String day = dateStr.substring(6, 8);
		String hh = dateStr.substring(8, 10);
		String mm = dateStr.substring(10, 12);
		String ss = dateStr.substring(12, 14);

		Calendar calendar = Calendar.getInstance();

		calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), Integer.parseInt(hh),
			Integer.parseInt(mm), Integer.parseInt(ss));

		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * 지난 요일 일자 가져오기
	 *
	 * @param pYoil - 가져올 요일( 1:일 2:월 ~ 6:금 7:토 )
	 * @return 해당요일의 일자 yyyyMMdd
	 */
	public static String getBeforeYoilDate(
		int pYoil) {
		String strDate = "";
		Calendar cal = Calendar.getInstance();
		int nDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int nDayOfYoil = 0;

		try {
			for (int i = 1; i <= 7; i++) {
				if (nDayOfWeek == i) {
					nDayOfYoil = pYoil - i;
					break;
				}
			}
			if (nDayOfYoil > 0)
				nDayOfYoil -= 7;
			cal.add(Calendar.DATE, nDayOfYoil);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
			strDate = sdf.format(cal.getTime());
		} catch (Exception e) {
			log.error("Exception !!!");
		}

		return strDate;
	}

	/**
	 * getConvertYyyymmdd
	 *
	 * @param tmp
	 * @return
	 */
	public static String getConvertYyyymmdd(
		Timestamp tmp,
		String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.KOREA);

		return sdf.format(tmp);
	}

	/**
	 * 데이터 형식이 맞는지 여부 확인(param : yyyy.MM.dd)
	 *
	 * @param dateString
	 * @return
	 */
	public static boolean isDateFormat(
		String dateString) {
		try {
			if (dateString == null || dateString.length() != 10) {
				return false;
			}

			String year = dateString.substring(0, 4);
			String month = dateString.substring(5, 7);
			String day = dateString.substring(8, 10);

			int yearInt = Integer.parseInt(year);
			int monthInt = Integer.parseInt(month);
			int dayInt = Integer.parseInt(day);

			Calendar calendar = Calendar.getInstance();
			calendar.set(yearInt, monthInt - 1, dayInt);

			if (yearInt < 0 || monthInt < 0 || dayInt < 0) {
				return false;
			}
			if (monthInt > 12 || dayInt > 31) {
				return false;
			}

			int endDay = getLastDayOfMon(yearInt, monthInt);
			if (Integer.parseInt(day) > endDay) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * 입력된 년월의 마지막 날
	 *
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public static int getLastDayOfMon(
		int year,
		int month) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);

		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 2개 날짜(Date)사이의 날짜들 리턴
	 * ex) getDatesOfRange("2010.11.30", "2010.12.02", "yyyy.MM.dd")
	 * getDatesOfRange
	 *
	 * @param beginDate
	 * @param endDate
	 * @param formatStr
	 * @return
	 * @throws Exception
	 */
	public static List<Date> getDatesOfRange(
		String beginDate,
		String endDate,
		String formatStr) throws Exception {
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(formatStr);
		return getDatesOfRange(format.parse(beginDate), format.parse(endDate));
	}

	/**
	 * 2개 날짜(Date)사이의 날짜들 리턴
	 * getDatesOfRange
	 *
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static List<Date> getDatesOfRange(
		Date beginDate,
		Date endDate) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(beginDate);

		Calendar endDateCalendar = new GregorianCalendar();
		endDateCalendar.setTime(endDate);
		endDateCalendar.add(Calendar.DATE, 1); // 마지막 날짜를 포함하기 위해 하루를 더함

		while (calendar.before(endDateCalendar)) {
			Date tempDate = calendar.getTime();
			dates.add(tempDate);
			calendar.add(Calendar.DATE, 1);
		}
		return dates;
	}

	public static List<String> getAddYyyy(
		int startYear,
		int addYear) {
		List<String> yearList = new ArrayList<String>();
		for (int i = 0; i < (Integer.parseInt(getYyyy()) + addYear - startYear); i++) {
			yearList.add(i, String.valueOf(startYear + i));
		}

		return yearList;
	}

	/**
	 *  현재년도를  yyyy형태로 반환한다.
	 *  @return String
	 */
	public static String getYyyy() {
		Calendar calendar = Calendar.getInstance();
		Locale currentLocale = new Locale("KOREAN", "KOREA");
		String pattern = "yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
		return formatter.format(calendar.getTime());
	}

	/**
	 * getDate
	 * 날짜 덧셈뺄셈
	 * @return
	 */
	public static String getDate(
		int year,
		int month,
		int day,
		int cc) {
		DecimalFormat df = new DecimalFormat("00");
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day);

		calendar.add(Calendar.DATE, cc);

		return (calendar.get(Calendar.YEAR) + "" + df.format(calendar.get(Calendar.MONTH) + 1) + "" + df.format(
			calendar.get(Calendar.DATE)));
	}

	/**
	 * 해당 일자에 대한 요일 Get
	 */
	public static String DayOfWeek(
		String pSDate,
		String pSDay) {
		String mWeek = "";

		Calendar calendar = Calendar.getInstance();
		int nYear = Integer.parseInt(pSDate.substring(0, 4));
		int nMonth = Integer.parseInt(pSDate.substring(4, 6));
		int nDay = Integer.parseInt(pSDay);
		calendar.set(nYear, nMonth - 1, nDay);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			mWeek = "Sun";
		} else if (dayOfWeek == 2) {
			mWeek = "Mon";
		} else if (dayOfWeek == 3) {
			mWeek = "Tue";
		} else if (dayOfWeek == 4) {
			mWeek = "Wed";
		} else if (dayOfWeek == 5) {
			mWeek = "Thu";
		} else if (dayOfWeek == 6) {
			mWeek = "Fri";
		} else if (dayOfWeek == 7) {
			mWeek = "Sat";
		}

		return mWeek;
	}

	/**
	 * 두 일자의 개월수를 리턴 (breakNow:true > sDate~현재월 , breakNow:false > sDate~eDate )
	 */
	public static int getDiffMonth(
		String psDate,
		String peDate,
		boolean breakNow) {
		String sDate = psDate;
		String eDate = peDate;
		int monthDiff = 0;
		String nDate = getYyyymmdd();
		sDate = sDate.replaceAll("\\D+", "");
		eDate = eDate.replaceAll("\\D+", "");

		int nYear = Integer.parseInt(nDate.substring(0, 4));
		int nMonth = Integer.parseInt(nDate.substring(4, 6));

		int sYear = Integer.parseInt(sDate.substring(0, 4));
		int sMonth = Integer.parseInt(sDate.substring(4, 6));

		int eYear = Integer.parseInt(eDate.substring(0, 4));
		int eMonth = Integer.parseInt(eDate.substring(4, 6));

		if (breakNow && eDate.substring(0, 6).compareTo(nDate.substring(0, 6)) > 0) {
			monthDiff = (nYear - sYear) * 12 + (nMonth - sMonth);
		} else {
			monthDiff = (eYear - sYear) * 12 + (eMonth - sMonth);
		}

		return monthDiff;
	}

	/**
	 * 현재날짜를 다양한 포멧으로 리턴한다. 예) getDate("yyyyMMdd"); getDate("yyyyMMddHHmmss");
	 * getDate("yyyy/MM/dd HH:mm:ss"); ex)
	 * getDate("yyyy/MM/dd"); getDate("HHmm");
	 */
	public static String getDate(
		String type) {
		if (type == null) {
			return null;
		}
		String s = "";
		s = new SimpleDateFormat(type, Locale.KOREA).format(new Date());
		return s;
	}

	/**
	 * 날짜(+시간)을 스트링으로 받어서 type 형태로 리턴한다. 예) formatDate("20030301","yyy/MM/dd")
	 * -> "2003/03/01" formatDate("20030301","yyyy-MM-dd")
	 * -> "2003-03-01" formatDate("2003030112","yyyy-MM-dd HH") ->
	 * "2003-03-01 12"
	 * formatDate("200303011200","yyyy-MM-dd HH:mm ss") -> "2003-03-01 12:00 00"
	 * formatDate("20030331115959","yyyy-MM-dd-HH-mm-ss") ->
	 * "2003-03-31-11-59-59"
	 *
	 * @param date
	 *            날짜포멧으로 변경될 문자열
	 * @param type
	 *            변경되어질 시간형식의 포멧
	 * @return 변경되어진 문자열
	 */
	public static String formatDate(
		String date,
		String type) throws Exception {
		if (date == null || type == null) {
			return null;
		}
		String result = "";
		int year = 0, month = 0, day = 0, hour = 0, min = 0, sec = 0, length = date.length();

		try {
			if (length >= 8) { // 날짜
				year = Integer.parseInt(date.substring(0, 4));
				month = Integer.parseInt(date.substring(4, 6)); // month 는 Calendar 에서 0 base 으로 작동하므로 1 을 빼준다.
				day = Integer.parseInt(date.substring(6, 8));
				if (length == 12) { // 날짜+시간
					hour = Integer.parseInt(date.substring(8, 10));
					min = Integer.parseInt(date.substring(10, 12));
				}
				if (length == 14) { // 날짜+시간
					hour = Integer.parseInt(date.substring(8, 10));
					min = Integer.parseInt(date.substring(10, 12));
					sec = Integer.parseInt(date.substring(12, 14));
				}
				Calendar cal = Calendar.getInstance();
				cal.set(year, month - 1, day, hour, min, sec);
				result = (new SimpleDateFormat(type, Locale.KOREA)).format(cal.getTime());
			}
		} catch (Exception ex) {
			/*
			 * [CWE-209] Coderay 취약점
			 * ex.getMessage() 제거
			 * @TODO 오류 메시지 대안 필요
			 * */
			log.error("DateUtil.formatDate(\"" + date + "\",\"" + type + "\")\r\n" + "Exception");
		}

		return result;
	}

	/**
	 * 현재일로 부터 해당 구분에 따라 월,일 등을 더한다.
	 *
	 * @param type
	 *            리턴되어질 날짜 형식
	 * @param gubn
	 *            더하고자 하는 날짜구분
	 * @param rec
	 *            더하고자 하는 수
	 * @return 계산되어진 날짜형식의 문자열
	 */
	public static String getDateAdd(
		String type,
		String gubn,
		int rec) throws Exception {
		String result = "";
		if (type == null)
			return null;
		try {
			Calendar cal = Calendar.getInstance();
			if (gubn.equals("month")) {
				cal.add(Calendar.MONTH, rec);
			}
			if (gubn.equals("date")) {
				cal.add(Calendar.DATE, rec);
			}
			if (gubn.equals("hour")) {
				cal.add(Calendar.HOUR_OF_DAY, rec);
			}
			if (gubn.equals("minute")) {
				cal.add(Calendar.MINUTE, rec);
			}
			if (gubn.equals("second")) {
				cal.add(Calendar.SECOND, rec);
			}
			result = (new SimpleDateFormat(type, Locale.KOREA)).format(cal.getTime());
		} catch (Exception ex) {
			log.error(
				"DateUtil.getDateAdd(\"" + type + "\",\"" + gubn + "\"," + rec + ")\r\n" + "getDateAdd Exception");
		}
		return result;
	}

	public static String getCurrentDateTime(String format) {
		return (new SimpleDateFormat(format)).format(new Date());
	}

	/**
	 * <p>
	 * Date -> String
	 * </p>
	 * @param date Date which you want to change.
	 * @return String The Date string. Type, yyyyMMdd HH:mm:ss.
	 */
	public static String toString(Date date, String format, Locale locale) {

		if (StringUtils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}

		if (locale == null) {
			locale = Locale.KOREA;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);

		String tmp = sdf.format(date);

		return tmp;
	}

	// === DateUtils에서 통합된 메서드들 ===
	
	/**
	 * 현재 년월 - YYYYMM (DateUtils에서 이동)
	 */
	public static String getMonth() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
	}

	/**
	 * 현재 년월일 - YYYYMMDD (DateUtils에서 이동)
	 */
	public static String getDate() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	/**
	 * 현재 시간 - HHMISS (DateUtils에서 이동)
	 */
	public static String getTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
	}

	/**
	 * 초단위시간 조회 (DateUtils에서 이동)
	 */
	public static String getSecTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	}

	/**
	 * 밀리세컨드초단위시간 조회 (DateUtils에서 이동)
	 */
	public static String getMillisecTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	}

	/**
	 * 특정날짜에 일자나 월수 더한 값 반환 (DateUtils에서 이동)
	 */
	public static String getDateCalculation(String dateTime, int plus, boolean isDay) {
		if (dateTime == null) return "";
		if (dateTime.startsWith("99991231")) return dateTime;

		String hms = "";
		if (dateTime.length() > 8) {
			hms = dateTime.substring(8);
		}

		LocalDate date = LocalDate.parse(dateTime.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
		
		if (isDay) {
			date = date.plusDays(plus);
		} else {
			date = date.plusMonths(plus);
		}

		return date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + hms;
	}

	/**
	 * 특정날짜에 일자를 더한 값 반환 (DateUtils에서 이동)
	 */
	public static String getAddDay(String date, int plusDay) {
		return getDateCalculation(date, plusDay, true);
	}

	/**
	 * 특정날짜에 달을 더한 값 반환 (DateUtils에서 이동)
	 */
	public static String getAddMonth(String date, int plusMonth) {
		return getDateCalculation(date, plusMonth, false);
	}

	/**
	 * 어제일자 - YYYYMMDD (DateUtils에서 이동)
	 */
	public static String getYesterday() {
		return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	/**
	 * 내일일자 - YYYYMMDD (DateUtils에서 이동)
	 */
	public static String getTomorrow() {
		return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	/**
	 * 일자 유효성 체크 (DateUtils에서 이동)
	 */
	public static boolean isDate(String str) {
		if (str == null || str.length() != 8) return false;
		
		try {
			LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * YYYY-MM-DD 형태로 반환 (DateUtils에서 이동)
	 */
	public static String getStringToDateForm(String strDt) {
		if (strDt == null || strDt.length() != 8) return "";
		
		try {
			LocalDate date = LocalDate.parse(strDt, DateTimeFormatter.ofPattern("yyyyMMdd"));
			return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * YYYYMMDDHH24MISS 유효성 체크 (DateUtils에서 이동)
	 */
	public static boolean isVaildDate(String str) {
		if (str == null || str.length() != 14) return false;
		
		try {
			LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 시간 유효성 체크 (HH24MISS) (DateUtils에서 이동)
	 */
	public static boolean isTime(String str) {
		if (str == null || str.length() != 6) return false;
		
		try {
			int hour = Integer.parseInt(str.substring(0, 2));
			int min = Integer.parseInt(str.substring(2, 4));
			int sec = Integer.parseInt(str.substring(4, 6));
			
			return hour >= 0 && hour <= 23 && min >= 0 && min <= 59 && sec >= 0 && sec <= 59;
		} catch (Exception e) {
			return false;
		}
	}
}
