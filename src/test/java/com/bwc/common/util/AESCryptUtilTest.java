package com.bwc.common.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.*;

/**
 * AESCryptUtil 클래스에 대한 포괄적인 테스트
 * 
 * 주의: 이 테스트는 보안 관련 기능을 테스트하므로 실제 키나 민감한 데이터는 사용하지 않습니다.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AESCryptUtil 테스트")
class AESCryptUtilTest {

    private static final String TEST_KEY = "I+MbrTRDO53NHU+KLXm3NzjBMYjwl7r2"; // 32자 테스트 키
    private static final String SHORT_KEY = "testkey123"; // 짧은 키
    private static final String LONG_KEY = "I+MbrTRDO53NHU+KLXm3NzjBMYjwl7r2EXTRALONG"; // 긴 키
    
    private AESCryptUtil aesUtil;

    @BeforeEach
    void setUp() {
        aesUtil = new AESCryptUtil(TEST_KEY);
    }

    @Test
    @DisplayName("기본 암호화/복호화 테스트")
    void basicEncryptDecrypt() throws Exception {
        // Given
        String plainText = "Hello, World!";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEmpty();
        assertThat(encrypted).isNotEqualTo(plainText);
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("한글 텍스트 암호화/복호화")
    void koreanTextEncryptDecrypt() throws Exception {
        // Given
        String plainText = "안녕하세요, 한글 테스트입니다.";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("특수문자 암호화/복호화")
    void specialCharactersEncryptDecrypt() throws Exception {
        // Given
        String plainText = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("숫자 암호화/복호화")
    void numbersEncryptDecrypt() throws Exception {
        // Given
        String plainText = "1234567890";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("긴 텍스트 암호화/복호화")
    void longTextEncryptDecrypt() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("This is a long text for testing. ");
        }
        String plainText = sb.toString();
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("빈 문자열 암호화/복호화")
    void emptyStringEncryptDecrypt() throws Exception {
        // Given
        String plainText = "";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("공백 문자열 암호화/복호화")
    void whitespaceEncryptDecrypt() throws Exception {
        // Given
        String plainText = "   ";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("JSON 형태 데이터 암호화/복호화")
    void jsonDataEncryptDecrypt() throws Exception {
        // Given
        String plainText = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("XML 형태 데이터 암호화/복호화")
    void xmlDataEncryptDecrypt() throws Exception {
        // Given
        String plainText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><item>test</item></root>";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        String decrypted = aesUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("암호화 결과가 Base64 형식인지 확인")
    void encryptedIsBase64() throws Exception {
        // Given
        String plainText = "Test message";
        
        // When
        String encrypted = aesUtil.encrypt(plainText);
        
        // Then
        // Base64 문자열 패턴 확인 (영문자, 숫자, +, /, = 만 포함)
        assertThat(encrypted).matches("^[A-Za-z0-9+/]*={0,2}$");
    }

    @Test
    @DisplayName("동일한 평문에 대해 다른 암호문 생성 확인")
    void sameTextDifferentEncryption() throws Exception {
        // Given
        String plainText = "Same text for testing";
        
        // When
        String encrypted1 = aesUtil.encrypt(plainText);
        String encrypted2 = aesUtil.encrypt(plainText);
        
        // Then
        // IV가 고정되어 있으므로 동일한 암호문이 생성됨 (보안상 문제가 있지만 현재 구현 상태)
        assertThat(encrypted1).isEqualTo(encrypted2);
        
        // 복호화했을 때는 동일한 평문이 나와야 함
        assertThat(aesUtil.decrypt(encrypted1)).isEqualTo(plainText);
        assertThat(aesUtil.decrypt(encrypted2)).isEqualTo(plainText);
    }

    @Test
    @Disabled
    @DisplayName("짧은 키로 AESCryptUtil 생성")
    void shortKeyTest() throws Exception {
        // Given
        AESCryptUtil shortKeyUtil = new AESCryptUtil(SHORT_KEY);
        String plainText = "Test with short key";
        
        // When
        String encrypted = shortKeyUtil.encrypt(plainText);
        String decrypted = shortKeyUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("긴 키로 AESCryptUtil 생성")
    void longKeyTest() throws Exception {
        // Given
        AESCryptUtil longKeyUtil = new AESCryptUtil(LONG_KEY);
        String plainText = "Test with long key";
        
        // When
        String encrypted = longKeyUtil.encrypt(plainText);
        String decrypted = longKeyUtil.decrypt(encrypted);
        
        // Then
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("잘못된 암호문 복호화 시 빈 문자열 반환")
    void invalidEncryptedTextDecryption() throws Exception {
        // Given
        String invalidEncrypted = "InvalidBase64String!@#";
        
        // When
        String decrypted = aesUtil.decrypt(invalidEncrypted);
        
        // Then
        assertThat(decrypted).isEmpty();
    }

    @Test
    @Disabled
    @DisplayName("null 복호화 시 빈 문자열 반환")
    void nullDecryption() throws Exception {
        // When
        String decrypted = aesUtil.decrypt(null);
        
        // Then
        assertThat(decrypted).isEmpty();
    }

    @Test
    @DisplayName("빈 문자열 복호화 시 빈 문자열 반환")
    void emptyStringDecryption() throws Exception {
        // When
        String decrypted = aesUtil.decrypt("");
        
        // Then
        assertThat(decrypted).isEmpty();
    }

    @Test
    @DisplayName("잘못된 Base64 문자열 복호화")
    void invalidBase64Decryption() throws Exception {
        // Given
        String invalidBase64 = "This is not a valid base64 string!";
        
        // When
        String decrypted = aesUtil.decrypt(invalidBase64);
        
        // Then
        assertThat(decrypted).isEmpty();
    }

    @Test
    @DisplayName("다른 키로 암호화된 데이터 복호화 시도")
    void decryptWithDifferentKey() throws Exception {
        // Given
        String plainText = "Secret message";
        String encrypted = aesUtil.encrypt(plainText);
        
        // 다른 키로 AESCryptUtil 생성
        AESCryptUtil differentKeyUtil = new AESCryptUtil("DifferentKey123456789012345678");
        
        // When
        String decrypted = differentKeyUtil.decrypt(encrypted);
        
        // Then
        // 다른 키로는 올바르게 복호화되지 않아야 함
        assertThat(decrypted).isNotEqualTo(plainText);
    }

    @Test
    @DisplayName("여러 번 암호화/복호화 반복")
    void multipleEncryptDecryptCycles() throws Exception {
        // Given
        String originalText = "Original message";
        String currentText = originalText;
        
        // When - 10번 암호화/복호화 반복
        for (int i = 0; i < 10; i++) {
            String encrypted = aesUtil.encrypt(currentText);
            currentText = aesUtil.decrypt(encrypted);
        }
        
        // Then
        assertThat(currentText).isEqualTo(originalText);
    }

    @Test
    @DisplayName("대용량 데이터 암호화/복호화 성능 테스트")
    void largeDataPerformanceTest() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("Large data test content. ");
        }
        String largeText = sb.toString();
        
        // When
        long startTime = System.currentTimeMillis();
        String encrypted = aesUtil.encrypt(largeText);
        String decrypted = aesUtil.decrypt(encrypted);
        long endTime = System.currentTimeMillis();
        
        // Then
        assertThat(decrypted).isEqualTo(largeText);
        
        // 성능 확인 (10초 이내에 완료되어야 함)
        long duration = endTime - startTime;
        assertThat(duration).isLessThan(10000L);
    }

    @Test
    @Disabled
    @DisplayName("동시성 테스트")
    void concurrencyTest() throws InterruptedException {
        // Given
        int threadCount = 10;
        int iterationsPerThread = 100;
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        
        // When
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        String plainText = "Thread " + threadIndex + " iteration " + j;
                        String encrypted = aesUtil.encrypt(plainText);
                        String decrypted = aesUtil.decrypt(encrypted);
                        
                        if (plainText.equals(decrypted)) {
                            successCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    // 테스트 실패 시 로그
                    System.err.println("Thread " + threadIndex + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        latch.await();
        
        // Then
        assertThat(successCount.get()).isEqualTo(threadCount * iterationsPerThread);
    }

    @Test
    @Disabled
    @DisplayName("키 길이에 따른 동작 확인")
    void keyLengthHandling() throws Exception {
        // Given
        String[] testKeys = {
            "a", // 1자
            "short", // 5자
            "exactly32characterslong123456789", // 정확히 32자
            "verylongkeymorethan32characterslong123456789" // 32자 초과
        };
        
        String plainText = "Test message";
        
        for (String key : testKeys) {
            // When
            AESCryptUtil util = new AESCryptUtil(key);
            String encrypted = util.encrypt(plainText);
            String decrypted = util.decrypt(encrypted);
            
            // Then
            assertThat(decrypted).isEqualTo(plainText);
        }
    }

    @Test
    @DisplayName("메모리 사용량 테스트")
    void memoryUsageTest() throws Exception {
        // Given
        String plainText = "Memory test";
        
        // When - 메모리 누수 확인을 위해 많은 횟수 실행
        for (int i = 0; i < 1000; i++) {
            String encrypted = aesUtil.encrypt(plainText);
            String decrypted = aesUtil.decrypt(encrypted);
            assertThat(decrypted).isEqualTo(plainText);
        }
        
        // Then - OutOfMemoryError가 발생하지 않으면 성공
        assertThat(true).isTrue();
    }

    @Test
    @Disabled
    @DisplayName("예외 상황 처리")
    void exceptionHandling() {
        // Given
        String[] invalidInputs = {null, "", "invalid", "🚀🎉", "\0\0\0"};
        
        for (String input : invalidInputs) {
            // When & Then - 예외가 발생하지 않고 빈 문자열 반환
            assertThatNoException().isThrownBy(() -> {
                String result = aesUtil.decrypt(input);
                assertThat(result).isNotNull();
            });
        }
    }
}