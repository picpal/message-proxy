package com.bwc.messaging.sms.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import com.bwc.messaging.shared.domain.MessageType;

@DisplayName("SMS 메시지 도메인 테스트")
class SmsMessageTest {

    @Test
    @DisplayName("유효한 SMS 메시지를 생성한다")
    void createValidSmsMessage() {
        // given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("test-001")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("안녕하세요")
            .build();

        // when & then
        assertThat(message.isValid()).isTrue();
        assertThat(message.getType()).isEqualTo(MessageType.SMS);
        assertThat(message.getSenderNumber()).isEqualTo("01012345678");
        assertThat(message.getReceiver()).isEqualTo("01087654321");
        assertThat(message.getContent()).isEqualTo("안녕하세요");
    }

    @Test
    @DisplayName("SMS 메시지 타입별 길이 제한을 검증한다")
    void validateMessageLength() {
        // given - SMS 길이 초과
        String longContent = "a".repeat(81); // 80자 초과
        SmsMessage smsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("test-002")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent)
            .build();

        // when & then
        assertThat(smsMessage.isValid()).isFalse();

        // given - LMS 유효한 길이
        SmsMessage lmsMessage = SmsMessage.builder(MessageType.LMS)
            .messageId("test-003")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent)
            .build();

        // when & then
        assertThat(lmsMessage.isValid()).isTrue();
    }

    @Test
    @DisplayName("전화번호 형식을 검증한다")
    void validatePhoneNumberFormat() {
        // given - 잘못된 수신자 번호
        SmsMessage invalidReceiver = SmsMessage.builder(MessageType.SMS)
            .messageId("test-004")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("invalid-number")
            .content("테스트 메시지")
            .build();

        // when & then
        assertThat(invalidReceiver.isValid()).isFalse();

        // given - 잘못된 발신자 번호
        SmsMessage invalidSender = SmsMessage.builder(MessageType.SMS)
            .messageId("test-005")
            .sender("TestApp")
            .senderNumber("invalid-sender")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();

        // when & then
        assertThat(invalidSender.isValid()).isFalse();
    }

    @Test
    @DisplayName("자동 타입 결정이 정상 동작한다")
    void autoTypeDecision() {
        // given - 짧은 메시지
        String shortContent = "짧은 메시지";
        SmsMessage shortMessage = SmsMessage.createAutoType(shortContent);
        shortMessage.setContent(shortContent);

        // when & then
        assertThat(shortMessage.getType()).isEqualTo(MessageType.SMS);

        // given - 긴 메시지
        String longContent = "a".repeat(100);
        SmsMessage longMessage = SmsMessage.createAutoType(longContent);
        longMessage.setContent(longContent);

        // when & then
        assertThat(longMessage.getType()).isEqualTo(MessageType.LMS);
    }
}