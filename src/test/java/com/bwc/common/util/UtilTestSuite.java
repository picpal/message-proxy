package com.bwc.common.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Common/Util 패키지의 통합 테스트
 * 실제 사용 케이스에 맞춘 현실적인 테스트
 */
@SpringBootTest
@TestPropertySource(properties = {"trx.guid.prifix=TEST"})
@DisplayName("Util 패키지 통합 테스트")
class UtilTestSuite {

    @Autowired
    private NtnoCrtnUtils ntnoCrtnUtils;

    @Test
    @DisplayName("DateUtil - 기본 날짜 기능 테스트")
    void testDateUtilBasicFunctions() {
        // 현재 날짜 관련 메서드들
        assertThat(DateUtil.getYyyymmdd()).hasSize(8).matches("\\d{8}");
        assertThat(DateUtil.getYyyymm()).hasSize(6).matches("\\d{6}");
        assertThat(DateUtil.getYyyymmddhhmmss()).hasSize(14).matches("\\d{14}");
        assertThat(DateUtil.getYyyy()).hasSize(4).matches("\\d{4}");
        
        // Java 8 time API 기반 메서드들
        assertThat(DateUtil.getDate()).hasSize(8).matches("\\d{8}");
        assertThat(DateUtil.getMonth()).hasSize(6).matches("\\d{6}");
        assertThat(DateUtil.getTime()).hasSize(6).matches("\\d{6}");
        assertThat(DateUtil.getSecTime()).hasSize(14).matches("\\d{14}");
        assertThat(DateUtil.getMillisecTime()).hasSize(17).matches("\\d{17}");
        
        // 날짜 계산
        assertThat(DateUtil.getAddDay("20231220", 5)).isEqualTo("20231225");
        assertThat(DateUtil.getAddMonth("20231020", 2)).isEqualTo("20231220");
        
        // 유효성 검사
        assertThat(DateUtil.isDate("20231225")).isTrue();
        assertThat(DateUtil.isDate("20231332")).isFalse();
        assertThat(DateUtil.isTime("143025")).isTrue();
        assertThat(DateUtil.isTime("256070")).isFalse();
    }

    @Test
    @DisplayName("NtnoCrtnUtils - GUID 생성 테스트")
    void testNtnoCrtnUtilsGuidGeneration() {
        // 거래 GUID 생성
        String trxGuid = ntnoCrtnUtils.occrTrxGuid();
        assertThat(trxGuid).isNotNull().isNotEmpty();
        assertThat(trxGuid).startsWith("TEST");
        
        // 세션 GUID 생성
        String sessionGuid = ntnoCrtnUtils.occrSessionGuid();
        assertThat(sessionGuid).isNotNull().isNotEmpty();
        assertThat(sessionGuid).startsWith("TEST");
        assertThat(sessionGuid).contains("SG");
        
        // 내부대체식별번호 생성
        String innrTrfrIdno = ntnoCrtnUtils.getInnrTrfrIdno("A");
        assertThat(innrTrfrIdno).isNotNull().isNotEmpty();
        assertThat(innrTrfrIdno).startsWith("A");
        
        // 시스템 프리픽스
        String systemPrefix = NtnoCrtnUtils.systemPrefix();
        assertThat(systemPrefix).hasSize(2).matches("\\d{2}");
        
        // 난수 생성
        String random = NtnoCrtnUtils.occrRmno(5);
        assertThat(random).hasSize(5).matches("\\d{5}");
    }

    @Test
    @DisplayName("AESCryptUtil - 기본 암호화/복호화 테스트")
    void testAESCryptUtilBasicFunctions() throws Exception {
        // 유효한 32자 키로 테스트
        String validKey = "12345678901234567890123456789012"; // 정확히 32자
        AESCryptUtil aesUtil = new AESCryptUtil(validKey);
        
        // 기본 암호화/복호화
        String plainText = "Hello, World!";
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        assertThat(encrypted).isNotNull().isNotEmpty();
        assertThat(encrypted).isNotEqualTo(plainText);
        assertThat(decrypted).isEqualTo(plainText);
        
        // 한글 텍스트
        String koreanText = "안녕하세요";
        String encryptedKorean = aesUtil.encrypt(koreanText);
        String decryptedKorean = aesUtil.decrypt(encryptedKorean);
        assertThat(decryptedKorean).isEqualTo(koreanText);
        
        // JSON 데이터
        String jsonText = "{\"name\":\"test\",\"value\":123}";
        String encryptedJson = aesUtil.encrypt(jsonText);
        String decryptedJson = aesUtil.decrypt(encryptedJson);
        assertThat(decryptedJson).isEqualTo(jsonText);
    }

    @Test
    @DisplayName("AESCryptUtil - 예외 처리 테스트")
    void testAESCryptUtilExceptionHandling() throws Exception {
        String validKey = "12345678901234567890123456789012";
        AESCryptUtil aesUtil = new AESCryptUtil(validKey);
        
        // 잘못된 암호문 복호화 시 빈 문자열 반환
        String decrypted1 = aesUtil.decrypt("invalid");
        assertThat(decrypted1).isEmpty();
        
        // 빈 문자열 복호화
        String decrypted2 = aesUtil.decrypt("");
        assertThat(decrypted2).isEmpty();
    }

    @Test
    @DisplayName("유틸리티 클래스들의 협업 테스트")
    void testUtilityClassesCooperation() throws Exception {
        // DateUtil로 시간 생성 -> NtnoCrtnUtils에서 GUID에 사용
        String currentTime = DateUtil.getMillisecTime();
        assertThat(currentTime).hasSize(17);
        
        String trxGuid = ntnoCrtnUtils.occrTrxGuid();
        // GUID에 현재 년도가 포함되어 있는지 확인
        String currentYear = currentTime.substring(0, 4);
        assertThat(trxGuid).contains(currentYear);
        
        // AESCryptUtil로 GUID 암호화
        String validKey = "12345678901234567890123456789012";
        AESCryptUtil aesUtil = new AESCryptUtil(validKey);
        
        String encryptedGuid = aesUtil.encrypt(trxGuid);
        String decryptedGuid = aesUtil.decrypt(encryptedGuid);
        assertThat(decryptedGuid).isEqualTo(trxGuid);
    }

    @Test
    @DisplayName("성능 테스트 - 대량 GUID 생성")
    void testPerformanceMassGuidGeneration() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            String trxGuid = ntnoCrtnUtils.occrTrxGuid();
            assertThat(trxGuid).isNotEmpty();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 1000개 GUID 생성이 5초 이내에 완료되어야 함
        assertThat(duration).isLessThan(5000L);
    }

    @Test
    @DisplayName("성능 테스트 - 대량 암호화/복호화")
    void testPerformanceMassEncryption() throws Exception {
        String validKey = "12345678901234567890123456789012";
        AESCryptUtil aesUtil = new AESCryptUtil(validKey);
        String testData = "Performance test data";
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            String encrypted = aesUtil.encrypt(testData);
            String decrypted = aesUtil.decrypt(encrypted);
            assertThat(decrypted).isEqualTo(testData);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 100회 암호화/복호화가 3초 이내에 완료되어야 함
        assertThat(duration).isLessThan(3000L);
    }

    @Test
    @DisplayName("날짜 형식 변환 테스트")
    void testDateFormatConversion() throws Exception {
        // 다양한 형식 변환
        assertThat(DateUtil.getStringToDateForm("20231225")).isEqualTo("2023-12-25");
        assertThat(DateUtil.formatDate("20231225", "yyyy-MM-dd")).isEqualTo("2023-12-25");
        assertThat(DateUtil.formatDate("20231225143025", "yyyy-MM-dd HH:mm:ss")).isEqualTo("2023-12-25 14:30:25");
        
        // 날짜 유효성 검사
        assertThat(DateUtil.isVaildDate("20231225143025")).isTrue();
        assertThat(DateUtil.isVaildDate("20231332253070")).isFalse();
    }

    @Test
    @DisplayName("시퀀스 생성 기본 테스트")
    void testSequenceGeneration() {
        // 기본 시퀀스 생성
        String seq1 = NtnoCrtnUtils.occrSqnc(2);
        String seq2 = NtnoCrtnUtils.occrSqnc(2);
        
        assertThat(seq1).hasSize(2).matches("\\d{2}");
        assertThat(seq2).hasSize(2).matches("\\d{2}");
        
        // 시퀀스는 순차적으로 증가해야 함
        int seqNum1 = Integer.parseInt(seq1);
        int seqNum2 = Integer.parseInt(seq2);
        assertThat(seqNum2).isEqualTo(seqNum1 + 1);
    }

    @Test
    @DisplayName("null 및 빈 값 처리")
    void testNullAndEmptyValueHandling() {
        // DateUtil null 처리
        assertThat(DateUtil.getDateCalculation(null, 1, true)).isEmpty();
        assertThat(DateUtil.isDate(null)).isFalse();
        assertThat(DateUtil.getStringToDateForm(null)).isEmpty();
        
        // DateUtil 빈 값 처리
        assertThat(DateUtil.isDate("")).isFalse();
        assertThat(DateUtil.getStringToDateForm("")).isEmpty();
    }
}