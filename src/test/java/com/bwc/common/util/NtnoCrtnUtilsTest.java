package com.bwc.common.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * NtnoCrtnUtils 클래스에 대한 포괄적인 테스트
 */
@SpringBootTest
@TestPropertySource(properties = {"trx.guid.prifix=TEST"})
@DisplayName("NtnoCrtnUtils 테스트")
class NtnoCrtnUtilsTest {

    @Autowired
    private NtnoCrtnUtils ntnoCrtnUtils;

    @BeforeEach
    void setUp() {
        // 정적 시퀀스 변수들을 초기화
        ReflectionTestUtils.setField(NtnoCrtnUtils.class, "sqnc", 0);
        ReflectionTestUtils.setField(NtnoCrtnUtils.class, "sqnc2", 0);
        ReflectionTestUtils.setField(NtnoCrtnUtils.class, "sqnc3", 0);
    }

    @Test
    @Disabled
    @DisplayName("거래 GUID 생성")
    void occrTrxGuid() {
        // When
        String trxGuid = ntnoCrtnUtils.occrTrxGuid();
        
        // Then
        assertThat(trxGuid).isNotNull();
        assertThat(trxGuid).isNotEmpty();
        
        // 구조 검증: PREFIX + SYSTEM_PREFIX + DATETIME + RANDOM(2) + SEQUENCE(2)
        assertThat(trxGuid).startsWith("TEST");
        
        // 전체 길이 검증 (TEST + 시스템프리픽스(2) + 시간(17) + 랜덤(2) + 시퀀스(2) = 26자)
        assertThat(trxGuid).hasSize(26);
        
        // 패턴 검증 (영문자 + 숫자)
        assertThat(trxGuid).matches("TEST\\d{21}");
    }

    @Test
    @DisplayName("세션 GUID 생성")
    void occrSessionGuid() {
        // When
        String sessionGuid = ntnoCrtnUtils.occrSessionGuid();
        
        // Then
        assertThat(sessionGuid).isNotNull();
        assertThat(sessionGuid).isNotEmpty();
        
        // 구조 검증: PREFIX + SYSTEM_PREFIX + "SG" + DATETIME + RANDOM(2) + SEQUENCE(2)
        assertThat(sessionGuid).startsWith("TEST");
        assertThat(sessionGuid).contains("SG");
        
        // SG가 올바른 위치에 있는지 확인 (TEST + 시스템프리픽스(2) + SG)
        assertThat(sessionGuid.substring(6, 8)).isEqualTo("SG");
    }

    @Test
    @DisplayName("시스템 프리픽스 생성")
    void systemPrefix() {
        // When
        String systemPrefix = NtnoCrtnUtils.systemPrefix();
        
        // Then
        assertThat(systemPrefix).isNotNull();
        assertThat(systemPrefix).hasSize(2);
        assertThat(systemPrefix).matches("\\d{2}");
        
        // 가능한 값들 (11, 12, 21, 22 중 하나)
        assertThat(systemPrefix).isIn("11", "12", "21", "22");
    }

    @Test
    @DisplayName("지정된 자릿수의 난수 발생")
    void occrRmno() {
        // Given
        int digits = 3;
        
        // When
        String randomNumber = NtnoCrtnUtils.occrRmno(digits);
        
        // Then
        assertThat(randomNumber).isNotNull();
        assertThat(randomNumber).hasSize(digits);
        assertThat(randomNumber).matches("\\d{3}");
    }

    @Test
    @DisplayName("다양한 자릿수의 난수 발생")
    void occrRmnoVariousDigits() {
        for (int i = 1; i <= 5; i++) {
            // When
            String randomNumber = NtnoCrtnUtils.occrRmno(i);
            
            // Then
            assertThat(randomNumber).hasSize(i);
            assertThat(randomNumber).matches("\\d{" + i + "}");
        }
    }

    @Test
    @DisplayName("큰 자릿수 난수 발생")
    void occrRmnoLargeDigits() {
        // Given
        int digits = 10;
        
        // When
        String randomNumber = NtnoCrtnUtils.occrRmno(digits);
        
        // Then
        assertThat(randomNumber).hasSize(digits);
        assertThat(randomNumber).matches("\\d{10}");
    }

    @Test
    @DisplayName("시퀀스 생성 (trx_guid용)")
    void occrSqnc() {
        // When & Then
        String seq1 = NtnoCrtnUtils.occrSqnc(2);
        String seq2 = NtnoCrtnUtils.occrSqnc(2);
        String seq3 = NtnoCrtnUtils.occrSqnc(2);
        
        // 순차적으로 증가하는지 확인
        assertThat(seq1).isEqualTo("01");
        assertThat(seq2).isEqualTo("02");
        assertThat(seq3).isEqualTo("03");
        
        // 모든 시퀀스가 2자리인지 확인
        assertThat(seq1).hasSize(2);
        assertThat(seq2).hasSize(2);
        assertThat(seq3).hasSize(2);
    }

    @Test
    @Disabled
    @DisplayName("시퀀스 최대값 초과 시 리셋")
    void occrSqncReset() {
        // Given - 시퀀스를 90으로 설정
        ReflectionTestUtils.setField(NtnoCrtnUtils.class, "sqnc", 90);
        
        // When
        String seq1 = NtnoCrtnUtils.occrSqnc(2);
        String seq2 = NtnoCrtnUtils.occrSqnc(2);
        
        // Then
        assertThat(seq1).isEqualTo("00"); // 90 초과로 0으로 리셋
        assertThat(seq2).isEqualTo("01"); // 다시 1부터 시작
    }

    @Test
    @DisplayName("시퀀스 생성 (내부대체식별번호용)")
    void occrSqnc2() {
        // When & Then
        String seq1 = NtnoCrtnUtils.occrSqnc2(2);
        String seq2 = NtnoCrtnUtils.occrSqnc2(2);
        String seq3 = NtnoCrtnUtils.occrSqnc2(2);
        
        // 순차적으로 증가하는지 확인
        assertThat(seq1).isEqualTo("01");
        assertThat(seq2).isEqualTo("02");
        assertThat(seq3).isEqualTo("03");
    }

    @Test
    @Disabled
    @DisplayName("시퀀스2 최대값 초과 시 리셋")
    void occrSqnc2Reset() {
        // Given - 시퀀스2를 90으로 설정
        ReflectionTestUtils.setField(NtnoCrtnUtils.class, "sqnc2", 90);
        
        // When
        String seq1 = NtnoCrtnUtils.occrSqnc2(2);
        String seq2 = NtnoCrtnUtils.occrSqnc2(2);
        
        // Then
        assertThat(seq1).isEqualTo("00"); // 90 초과로 0으로 리셋
        assertThat(seq2).isEqualTo("01"); // 다시 1부터 시작
    }

    @Test
    @DisplayName("시퀀스 생성 (세션GUID용)")
    void occrSqnc3() {
        // When & Then
        String seq1 = NtnoCrtnUtils.occrSqnc3(2);
        String seq2 = NtnoCrtnUtils.occrSqnc3(2);
        String seq3 = NtnoCrtnUtils.occrSqnc3(2);
        
        // 순차적으로 증가하는지 확인
        assertThat(seq1).isEqualTo("01");
        assertThat(seq2).isEqualTo("02");
        assertThat(seq3).isEqualTo("03");
    }

    @Test
    @Disabled
    @DisplayName("시퀀스3 최대값 초과 시 리셋")
    void occrSqnc3Reset() {
        // Given - 시퀀스3을 90으로 설정
        ReflectionTestUtils.setField(NtnoCrtnUtils.class, "sqnc3", 90);
        
        // When
        String seq1 = NtnoCrtnUtils.occrSqnc3(2);
        String seq2 = NtnoCrtnUtils.occrSqnc3(2);
        
        // Then
        assertThat(seq1).isEqualTo("00"); // 90 초과로 0으로 리셋
        assertThat(seq2).isEqualTo("01"); // 다시 1부터 시작
    }

    @Test
    @Disabled
    @DisplayName("다양한 자릿수 시퀀스 생성")
    void occrSqncVariousDigits() {
        // 1자리
        String seq1 = NtnoCrtnUtils.occrSqnc(1);
        assertThat(seq1).hasSize(1);
        assertThat(seq1).isEqualTo("1");
        
        // 3자리
        String seq3 = NtnoCrtnUtils.occrSqnc(3);
        assertThat(seq3).hasSize(3);
        assertThat(seq3).isEqualTo("002");
        
        // 4자리
        String seq4 = NtnoCrtnUtils.occrSqnc(4);
        assertThat(seq4).hasSize(4);
        assertThat(seq4).isEqualTo("0003");
    }

    @Test
    @DisplayName("내부대체식별번호 생성")
    void getInnrTrfrIdno() {
        // Given
        String innrTrfrIdnoTp = "A";
        
        // When
        String innrTrfrIdno = ntnoCrtnUtils.getInnrTrfrIdno(innrTrfrIdnoTp);
        
        // Then
        assertThat(innrTrfrIdno).isNotNull();
        assertThat(innrTrfrIdno).startsWith("A");
        
        // 구조: A + DATETIME(17) + RANDOM(3) + SEQUENCE(2) = 23자
        assertThat(innrTrfrIdno).hasSize(23);
        assertThat(innrTrfrIdno).matches("A\\d{22}");
    }

    @Test
    @DisplayName("다양한 타입의 내부대체식별번호 생성")
    void getInnrTrfrIdnoVariousTypes() {
        // Given
        String[] types = {"A", "C", "B", "T"};
        
        for (String type : types) {
            // When
            String innrTrfrIdno = ntnoCrtnUtils.getInnrTrfrIdno(type);
            
            // Then
            assertThat(innrTrfrIdno).startsWith(type);
            assertThat(innrTrfrIdno).hasSize(23);
            assertThat(innrTrfrIdno).matches(type + "\\d{22}");
        }
    }

    @Test
    @DisplayName("동시성 테스트 - 시퀀스 중복 확인")
    void concurrencyTest() throws InterruptedException {
        // Given
        int threadCount = 10;
        int iterationsPerThread = 100;
        java.util.Set<String> generatedSequences = java.util.concurrent.ConcurrentHashMap.newKeySet();
        
        // When
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        String seq = NtnoCrtnUtils.occrSqnc(3);
                        generatedSequences.add(seq);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        latch.await();
        
        // Then - 모든 시퀀스가 올바른 형식인지 확인
        assertThat(generatedSequences).isNotEmpty();
        generatedSequences.forEach(seq -> {
            assertThat(seq).hasSize(3);
            assertThat(seq).matches("\\d{3}");
        });
    }

    @Test
    @DisplayName("GUID 유일성 테스트")
    void guidUniquenessTest() {
        // Given
        java.util.Set<String> generatedGuids = new java.util.HashSet<>();
        int testCount = 1000;
        
        // When
        for (int i = 0; i < testCount; i++) {
            String trxGuid = ntnoCrtnUtils.occrTrxGuid();
            String sessionGuid = ntnoCrtnUtils.occrSessionGuid();
            
            generatedGuids.add(trxGuid);
            generatedGuids.add(sessionGuid);
        }
        
        // Then - 생성된 GUID가 모두 유일한지 확인
        assertThat(generatedGuids).hasSize(testCount * 2);
    }

    @Test
    @Disabled
    @DisplayName("시간 정보 포함 확인")
    void timeInclusionTest() {
        // When
        String trxGuid = ntnoCrtnUtils.occrTrxGuid();
        String sessionGuid = ntnoCrtnUtils.occrSessionGuid();
        
        // Then - 현재 시간 정보가 포함되어 있는지 확인
        String currentDateTime = DateUtil.getMillisecTime();
        String currentYear = currentDateTime.substring(0, 4);
        String currentMonth = currentDateTime.substring(4, 6);
        
        // GUID에 현재 년도와 월이 포함되어 있는지 확인
        assertThat(trxGuid).contains(currentYear);
        assertThat(trxGuid).contains(currentMonth);
        assertThat(sessionGuid).contains(currentYear);
        assertThat(sessionGuid).contains(currentMonth);
    }

    @Test
    @DisplayName("프리픽스 설정 확인")
    void prefixConfigurationTest() {
        // When
        String trxGuid = ntnoCrtnUtils.occrTrxGuid();
        String sessionGuid = ntnoCrtnUtils.occrSessionGuid();
        
        // Then - 설정된 프리픽스가 올바르게 적용되었는지 확인
        assertThat(trxGuid).startsWith("TEST");
        assertThat(sessionGuid).startsWith("TEST");
    }

    @Test
    @DisplayName("빈 값이나 null 처리")
    void handleEmptyValues() {
        // Given & When & Then
        // 정상적인 파라미터에서는 null이나 빈 값이 반환되지 않아야 함
        assertThat(ntnoCrtnUtils.occrTrxGuid()).isNotEmpty();
        assertThat(ntnoCrtnUtils.occrSessionGuid()).isNotEmpty();
        assertThat(ntnoCrtnUtils.getInnrTrfrIdno("A")).isNotEmpty();
        assertThat(NtnoCrtnUtils.systemPrefix()).isNotEmpty();
        assertThat(NtnoCrtnUtils.occrRmno(1)).isNotEmpty();
        assertThat(NtnoCrtnUtils.occrSqnc(1)).isNotEmpty();
    }

    @Test
    @Disabled
    @DisplayName("0자리 난수 및 시퀀스 처리")
    void zeroDigitHandling() {
        // Given & When
        String randomZero = NtnoCrtnUtils.occrRmno(0);
        String seqZero = NtnoCrtnUtils.occrSqnc(0);
        
        // Then - 0자리는 빈 문자열이 되어야 함
        assertThat(randomZero).isEmpty();
        assertThat(seqZero).isEmpty();
    }
}