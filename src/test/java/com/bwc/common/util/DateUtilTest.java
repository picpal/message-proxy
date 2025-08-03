package com.bwc.common.util;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * DateUtil 클래스에 대한 포괄적인 테스트
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("DateUtil 테스트")
class DateUtilTest {

    @Test
    @DisplayName("현재 Timestamp 생성")
    void getCurrentTimestamp() {
        // Given & When
        Timestamp timestamp = DateUtil.getCurrentTimestamp();
        
        // Then
        assertThat(timestamp).isNotNull();
        assertThat(timestamp.getTime()).isCloseTo(System.currentTimeMillis(), within(1000L));
    }

    @Test
    @DisplayName("현재 년월(YYYYMM) 반환")
    void getYyyymm() {
        // Given & When
        String yyyymm = DateUtil.getYyyymm();
        
        // Then
        assertThat(yyyymm).isNotNull();
        assertThat(yyyymm).hasSize(6);
        assertThat(yyyymm).matches("\\d{6}");
        
        // 현재 년월과 비교
        String expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        assertThat(yyyymm).isEqualTo(expected);
    }

    @Test
    @DisplayName("현재 년월일(YYYYMMDD) 반환")
    void getYyyymmdd() {
        // Given & When
        String yyyymmdd = DateUtil.getYyyymmdd();
        
        // Then
        assertThat(yyyymmdd).isNotNull();
        assertThat(yyyymmdd).hasSize(8);
        assertThat(yyyymmdd).matches("\\d{8}");
        
        // 현재 날짜와 비교
        String expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(yyyymmdd).isEqualTo(expected);
    }

    @Test
    @DisplayName("현재 년월일시분초(YYYYMMDDhhmmss) 반환")
    void getYyyymmddhhmmss() {
        // Given & When
        String timestamp = DateUtil.getYyyymmddhhmmss();
        
        // Then
        assertThat(timestamp).isNotNull();
        assertThat(timestamp).hasSize(14);
        assertThat(timestamp).matches("\\d{14}");
        
        // 현재 시간과 비교 (초 단위까지는 허용 오차)
        String expected = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        assertThat(timestamp).isEqualTo(expected);
    }

    @Test
    @DisplayName("Calendar 객체로부터 날짜 문자열 생성")
    void getYyyymmddWithCalendar() {
        // Given
        Calendar cal = new GregorianCalendar(2023, Calendar.DECEMBER, 25);
        
        // When
        String result = DateUtil.getYyyymmdd(cal);
        
        // Then
        assertThat(result).isEqualTo("2023.12.25");
    }

    @Test
    @DisplayName("패턴으로 현재 날짜 반환")
    void getNowDate() {
        // Given
        String pattern = "yyyy-MM-dd HH:mm";
        
        // When
        String result = DateUtil.getNowDate(pattern);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    }

    @Test
    @DisplayName("GregorianCalendar 생성")
    void getGregorianCalendar() {
        // Given
        String yyyymmdd = "20231225";
        
        // When
        GregorianCalendar calendar = DateUtil.getGregorianCalendar(yyyymmdd);
        
        // Then
        assertThat(calendar.get(Calendar.YEAR)).isEqualTo(2023);
        assertThat(calendar.get(Calendar.MONTH)).isEqualTo(Calendar.DECEMBER);
        assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(25);
    }

    @Test
    @DisplayName("날짜 연산 - 일자 추가")
    void getOpDateAddDays() {
        // Given
        String baseDate = "20231220";
        
        // When
        String result = DateUtil.getOpDate(Calendar.DATE, 5, baseDate);
        
        // Then
        assertThat(result).isEqualTo("2023.12.25");
    }

    @Test
    @DisplayName("날짜 연산 - 월 추가")
    void getOpDateAddMonths() {
        // Given
        String baseDate = "20231020";
        
        // When
        String result = DateUtil.getOpDate(Calendar.MONTH, 2, baseDate);
        
        // Then
        assertThat(result).isEqualTo("2023.12.20");
    }

    @Test
    @DisplayName("날짜 연산 - 년 추가")
    void getOpDateAddYears() {
        // Given
        String baseDate = "20221220";
        
        // When
        String result = DateUtil.getOpDate(Calendar.YEAR, 1, baseDate);
        
        // Then
        assertThat(result).isEqualTo("2023.12.20");
    }

    @Test
    @DisplayName("문자열을 Timestamp로 변환 (yyyy-MM-dd 형식)")
    void replaceTimestamp() {
        // Given
        String dateStr = "2023-12-25";
        
        // When
        Timestamp timestamp = DateUtil.replaceTimestamp(dateStr);
        
        // Then
        assertThat(timestamp).isNotNull();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        assertThat(cal.get(Calendar.YEAR)).isEqualTo(2023);
        assertThat(cal.get(Calendar.MONTH)).isEqualTo(Calendar.DECEMBER);
        assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(25);
    }

    @Test
    @DisplayName("잘못된 형식의 문자열로 Timestamp 변환 시 null 반환")
    void replaceTimestampInvalidFormat() {
        // Given
        String invalidDateStr = "2023-1-1";
        
        // When
        Timestamp timestamp = DateUtil.replaceTimestamp(invalidDateStr);
        
        // Then
        assertThat(timestamp).isNull();
    }

    @Test
    @DisplayName("yyyyMMddHHmmss 형식을 Timestamp로 변환")
    void replaceTimestampType1() {
        // Given
        String dateStr = "20231225143025";
        
        // When
        Timestamp timestamp = DateUtil.replaceTimestampType1(dateStr);
        
        // Then
        assertThat(timestamp).isNotNull();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        assertThat(cal.get(Calendar.YEAR)).isEqualTo(2023);
        assertThat(cal.get(Calendar.MONTH)).isEqualTo(Calendar.DECEMBER);
        assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(25);
        assertThat(cal.get(Calendar.HOUR_OF_DAY)).isEqualTo(14);
        assertThat(cal.get(Calendar.MINUTE)).isEqualTo(30);
        assertThat(cal.get(Calendar.SECOND)).isEqualTo(25);
    }

    @Test
    @DisplayName("지난 요일 날짜 구하기 - 일요일")
    void getBeforeYoilDateSunday() {
        // When
        String result = DateUtil.getBeforeYoilDate(1); // 1 = 일요일
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).matches("\\d{8}");
    }

    @Test
    @DisplayName("Timestamp를 문자열로 변환")
    void getConvertYyyymmdd() {
        // Given
        Timestamp timestamp = Timestamp.valueOf("2023-12-25 14:30:25.123");
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        
        // When
        String result = DateUtil.getConvertYyyymmdd(timestamp, dateFormat);
        
        // Then
        assertThat(result).isEqualTo("2023-12-25 14:30:25");
    }

    @Test
    @DisplayName("날짜 형식 유효성 검사 - 유효한 날짜")
    void isDateFormatValid() {
        // Given
        String validDate = "2023.12.25";
        
        // When
        boolean result = DateUtil.isDateFormat(validDate);
        
        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("날짜 형식 유효성 검사 - 무효한 날짜")
    void isDateFormatInvalid() {
        // Given
        String invalidDate = "2023.13.32";
        
        // When
        boolean result = DateUtil.isDateFormat(invalidDate);
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("월의 마지막 날 구하기")
    void getLastDayOfMon() throws Exception {
        // Given & When
        int lastDay = DateUtil.getLastDayOfMon(2023, 2); // 2023년 2월
        
        // Then
        assertThat(lastDay).isEqualTo(28); // 2023년은 평년
    }

    @Test
    @DisplayName("윤년의 2월 마지막 날 구하기")
    void getLastDayOfMonLeapYear() throws Exception {
        // Given & When
        int lastDay = DateUtil.getLastDayOfMon(2024, 2); // 2024년 2월 (윤년)
        
        // Then
        assertThat(lastDay).isEqualTo(29);
    }

    @Test
    @DisplayName("날짜 범위의 모든 날짜 구하기")
    void getDatesOfRange() throws Exception {
        // Given
        String beginDate = "2023.12.25";
        String endDate = "2023.12.27";
        String formatStr = "yyyy.MM.dd";
        
        // When
        List<Date> dates = DateUtil.getDatesOfRange(beginDate, endDate, formatStr);
        
        // Then
        assertThat(dates).hasSize(3); // 25, 26, 27일
    }

    @Test
    @DisplayName("현재 년도 반환")
    void getYyyy() {
        // When
        String year = DateUtil.getYyyy();
        
        // Then
        assertThat(year).isNotNull();
        assertThat(year).hasSize(4);
        assertThat(year).matches("\\d{4}");
        
        String currentYear = String.valueOf(LocalDate.now().getYear());
        assertThat(year).isEqualTo(currentYear);
    }

    @Test
    @DisplayName("날짜 덧셈뺄셈")
    void getDate() {
        // Given
        int year = 2023;
        int month = 12;
        int day = 20;
        int addDays = 5;
        
        // When
        String result = DateUtil.getDate(year, month, day, addDays);
        
        // Then
        assertThat(result).isEqualTo("20231225");
    }

    @Test
    @DisplayName("요일 구하기")
    void dayOfWeek() {
        // Given
        String date = "202312"; // 2023년 12월
        String day = "25"; // 25일
        
        // When
        String dayOfWeek = DateUtil.DayOfWeek(date, day);
        
        // Then
        assertThat(dayOfWeek).isIn("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
    }

    @Test
    @DisplayName("두 날짜 간의 개월 수 차이")
    void getDiffMonth() {
        // Given
        String startDate = "20230101";
        String endDate = "20231201";
        
        // When
        int monthDiff = DateUtil.getDiffMonth(startDate, endDate, false);
        
        // Then
        assertThat(monthDiff).isEqualTo(11);
    }

    @Test
    @DisplayName("현재 날짜를 다양한 포맷으로 반환")
    void getDateWithFormat() {
        // Given
        String format = "yyyy-MM-dd";
        
        // When
        String result = DateUtil.getDate(format);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Test
    @DisplayName("날짜 문자열 포맷 변환")
    void formatDate() throws Exception {
        // Given
        String date = "20231225";
        String format = "yyyy-MM-dd";
        
        // When
        String result = DateUtil.formatDate(date, format);
        
        // Then
        assertThat(result).isEqualTo("2023-12-25");
    }

    @Test
    @DisplayName("날짜 더하기 - 월 단위")
    void getDateAddMonth() throws Exception {
        // Given
        String type = "yyyy-MM-dd";
        String gubn = "month";
        int addMonths = 2;
        
        // When
        String result = DateUtil.getDateAdd(type, gubn, addMonths);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Test
    @DisplayName("현재 날짜시간을 지정 포맷으로 반환")
    void getCurrentDateTime() {
        // Given
        String format = "yyyyMMddHHmmss";
        
        // When
        String result = DateUtil.getCurrentDateTime(format);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(14);
        assertThat(result).matches("\\d{14}");
    }

    @Test
    @DisplayName("Date 객체를 문자열로 변환")
    void toStringWithDate() {
        // Given
        Date date = new Date();
        String format = "yyyy-MM-dd HH:mm:ss";
        Locale locale = Locale.KOREA;
        
        // When
        String result = DateUtil.toString(date, format, locale);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }

    // === DateUtils에서 통합된 메서드들 테스트 ===

    @Test
    @DisplayName("현재 년월 반환 (YYYYMM)")
    void getMonth() {
        // When
        String month = DateUtil.getMonth();
        
        // Then
        assertThat(month).isNotNull();
        assertThat(month).hasSize(6);
        assertThat(month).matches("\\d{6}");
        
        String expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        assertThat(month).isEqualTo(expected);
    }

    @Test
    @DisplayName("현재 년월일 반환 (YYYYMMDD)")
    void getDate_() {
        // When
        String date = DateUtil.getDate();
        
        // Then
        assertThat(date).isNotNull();
        assertThat(date).hasSize(8);
        assertThat(date).matches("\\d{8}");
        
        String expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(date).isEqualTo(expected);
    }

    @Test
    @DisplayName("현재 시간 반환 (HHMMSS)")
    void getTime() {
        // When
        String time = DateUtil.getTime();
        
        // Then
        assertThat(time).isNotNull();
        assertThat(time).hasSize(6);
        assertThat(time).matches("\\d{6}");
    }

    @Test
    @DisplayName("초단위 시간 조회 (YYYYMMDDHHMMSS)")
    void getSecTime() {
        // When
        String secTime = DateUtil.getSecTime();
        
        // Then
        assertThat(secTime).isNotNull();
        assertThat(secTime).hasSize(14);
        assertThat(secTime).matches("\\d{14}");
    }

    @Test
    @DisplayName("밀리세컨드 초단위 시간 조회 (YYYYMMDDHHMMSSSS)")
    void getMillisecTime() {
        // When
        String millisecTime = DateUtil.getMillisecTime();
        
        // Then
        assertThat(millisecTime).isNotNull();
        assertThat(millisecTime).hasSize(17);
        assertThat(millisecTime).matches("\\d{17}");
    }

    @Test
    @DisplayName("특정 날짜에 일자 더하기")
    void getDateCalculationAddDays() {
        // Given
        String dateTime = "20231220";
        int plusDays = 5;
        
        // When
        String result = DateUtil.getDateCalculation(dateTime, plusDays, true);
        
        // Then
        assertThat(result).isEqualTo("20231225");
    }

    @Test
    @DisplayName("특정 날짜에 월수 더하기")
    void getDateCalculationAddMonths() {
        // Given
        String dateTime = "20231020";
        int plusMonths = 2;
        
        // When
        String result = DateUtil.getDateCalculation(dateTime, plusMonths, false);
        
        // Then
        assertThat(result).isEqualTo("20231220");
    }

    @Test
    @DisplayName("시간 정보가 포함된 날짜 계산")
    void getDateCalculationWithTime() {
        // Given
        String dateTime = "20231220143025";
        int plusDays = 5;
        
        // When
        String result = DateUtil.getDateCalculation(dateTime, plusDays, true);
        
        // Then
        assertThat(result).isEqualTo("20231225143025");
    }

    @Test
    @DisplayName("특수 날짜 처리 (99991231)")
    void getDateCalculationSpecialDate() {
        // Given
        String dateTime = "99991231";
        int plusDays = 5;
        
        // When
        String result = DateUtil.getDateCalculation(dateTime, plusDays, true);
        
        // Then
        assertThat(result).isEqualTo("99991231");
    }

    @Test
    @DisplayName("일자 더하기 편의 메서드")
    void getAddDay() {
        // Given
        String date = "20231220";
        int plusDay = 5;
        
        // When
        String result = DateUtil.getAddDay(date, plusDay);
        
        // Then
        assertThat(result).isEqualTo("20231225");
    }

    @Test
    @DisplayName("월 더하기 편의 메서드")
    void getAddMonth() {
        // Given
        String date = "20231020";
        int plusMonth = 2;
        
        // When
        String result = DateUtil.getAddMonth(date, plusMonth);
        
        // Then
        assertThat(result).isEqualTo("20231220");
    }

    @Test
    @DisplayName("어제 일자 반환")
    void getYesterday() {
        // When
        String yesterday = DateUtil.getYesterday();
        
        // Then
        assertThat(yesterday).isNotNull();
        assertThat(yesterday).hasSize(8);
        assertThat(yesterday).matches("\\d{8}");
        
        String expected = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(yesterday).isEqualTo(expected);
    }

    @Test
    @DisplayName("내일 일자 반환")
    void getTomorrow() {
        // When
        String tomorrow = DateUtil.getTomorrow();
        
        // Then
        assertThat(tomorrow).isNotNull();
        assertThat(tomorrow).hasSize(8);
        assertThat(tomorrow).matches("\\d{8}");
        
        String expected = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(tomorrow).isEqualTo(expected);
    }

    @Test
    @DisplayName("일자 유효성 체크 - 유효한 날짜")
    void isDateValid() {
        // Given
        String validDate = "20231225";
        
        // When
        boolean result = DateUtil.isDate(validDate);
        
        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("일자 유효성 체크 - 무효한 날짜")
    void isDateInvalid() {
        // Given
        String invalidDate = "20231332";
        
        // When
        boolean result = DateUtil.isDate(invalidDate);
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("YYYY-MM-DD 형태로 변환")
    void getStringToDateForm() {
        // Given
        String dateStr = "20231225";
        
        // When
        String result = DateUtil.getStringToDateForm(dateStr);
        
        // Then
        assertThat(result).isEqualTo("2023-12-25");
    }

    @Test
    @DisplayName("YYYYMMDDHH24MISS 유효성 체크 - 유효한 날짜시간")
    void isVaildDateValid() {
        // Given
        String validDateTime = "20231225143025";
        
        // When
        boolean result = DateUtil.isVaildDate(validDateTime);
        
        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("YYYYMMDDHH24MISS 유효성 체크 - 무효한 날짜시간")
    void isVaildDateInvalid() {
        // Given
        String invalidDateTime = "20231332253070";
        
        // When
        boolean result = DateUtil.isVaildDate(invalidDateTime);
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("시간 유효성 체크 - 유효한 시간")
    void isTimeValid() {
        // Given
        String validTime = "143025";
        
        // When
        boolean result = DateUtil.isTime(validTime);
        
        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("시간 유효성 체크 - 무효한 시간")
    void isTimeInvalid() {
        // Given
        String invalidTime = "256070";
        
        // When
        boolean result = DateUtil.isTime(invalidTime);
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("null 입력 처리")
    void handleNullInputs() {
        // Test null inputs
        assertThat(DateUtil.getDateCalculation(null, 1, true)).isEmpty();
        assertThat(DateUtil.replaceTimestamp(null)).isNull();
        assertThat(DateUtil.replaceTimestampType1(null)).isNull();
        assertThat(DateUtil.isDate(null)).isFalse();
        assertThat(DateUtil.isVaildDate(null)).isFalse();
        assertThat(DateUtil.isTime(null)).isFalse();
        assertThat(DateUtil.getStringToDateForm(null)).isEmpty();
    }
}