package com.bwc.messaging.sms.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bwc.messaging.shared.domain.MessageType;

@DisplayName("SmsMessage 테스트")
class SmsMessageTest {

    @Test
    @DisplayName("SMS 메시지 생성 성공")
    void createSmsMessage_Success() {
        // When
        SmsMessage sms = new SmsMessage(MessageType.SMS);
        
        // Then
        assertNotNull(sms);
        assertEquals(MessageType.SMS, sms.getType());
    }
    
    @Test
    @DisplayName("LMS 메시지 생성 성공")
    void createLmsMessage_Success() {
        // When
        SmsMessage lms = new SmsMessage(MessageType.LMS);
        
        // Then
        assertNotNull(lms);
        assertEquals(MessageType.LMS, lms.getType());
    }
    
    @Test
    @DisplayName("MMS 메시지 생성 성공")
    void createMmsMessage_Success() {
        // When
        SmsMessage mms = new SmsMessage(MessageType.MMS);
        
        // Then
        assertNotNull(mms);
        assertEquals(MessageType.MMS, mms.getType());
    }
    
    @Test
    @DisplayName("EMAIL 타입으로 SMS 메시지 생성 실패")
    void createSmsMessage_InvalidType_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SmsMessage(MessageType.EMAIL)
        );
        
        assertEquals("Invalid SMS type: EMAIL", exception.getMessage());
    }
    
    @Test
    @DisplayName("유효한 SMS 메시지 검증")
    void isValid_ValidSmsMessage() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-001")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("유효한 SMS 메시지")
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    @DisplayName("유효한 LMS 메시지 검증")
    void isValid_ValidLmsMessage() {
        // Given
        String longContent = "이것은 긴 LMS 메시지입니다. ".repeat(10);
        SmsMessage message = SmsMessage.builder(MessageType.LMS)
            .messageId("MSG-002")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent)
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    @DisplayName("무효한 수신자 번호로 검증 실패")
    void isValid_InvalidReceiver() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-003")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("invalid-number")
            .content("테스트 메시지")
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("무효한 발신자 번호로 검증 실패")
    void isValid_InvalidSenderNumber() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-004")
            .sender("TEST")
            .senderNumber("invalid-sender")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("빈 내용으로 검증 실패")
    void isValid_EmptyContent() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-005")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("")
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("SMS 길이 초과로 검증 실패")
    void isValid_SmsContentTooLong() {
        // Given
        String longContent = "a".repeat(81); // SMS 최대 길이(80자) 초과
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-006")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent)
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("LMS 길이 초과로 검증 실패")
    void isValid_LmsContentTooLong() {
        // Given
        String longContent = "a".repeat(2001); // LMS 최대 길이(2000자) 초과
        SmsMessage message = SmsMessage.builder(MessageType.LMS)
            .messageId("MSG-007")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent)
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    @DisplayName("SMS 메시지 발송 파라미터 생성")
    void toSendParameters_SmsMessage() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-008")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("SMS 메시지")
            .templateCode("TEMPLATE_001")
            .build();
        
        // When
        Map<String, Object> params = message.toSendParameters();
        
        // Then
        assertEquals("MSG-008", params.get("messageId"));
        assertEquals("SMS", params.get("type"));
        assertEquals("TEST", params.get("sender"));
        assertEquals("01012345678", params.get("senderNumber"));
        assertEquals("01087654321", params.get("receiver"));
        assertEquals("SMS 메시지", params.get("content"));
        assertEquals("TEMPLATE_001", params.get("templateCode"));
        assertFalse(params.containsKey("subject"));
    }
    
    @Test
    @DisplayName("LMS 메시지 발송 파라미터 생성 (subject 포함)")
    void toSendParameters_LmsMessage() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.LMS)
            .messageId("MSG-009")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("긴 LMS 메시지")
            .subject("LMS 제목")
            .build();
        
        // When
        Map<String, Object> params = message.toSendParameters();
        
        // Then
        assertEquals("MSG-009", params.get("messageId"));
        assertEquals("LMS", params.get("type"));
        assertEquals("LMS 제목", params.get("subject"));
    }
    
    @Test
    @DisplayName("자동 타입 결정 - SMS")
    void createAutoType_Sms() {
        // Given
        String shortContent = "짧은 메시지";
        
        // When
        SmsMessage message = SmsMessage.createAutoType(shortContent);
        
        // Then
        assertEquals(MessageType.SMS, message.getType());
    }
    
    @Test
    @DisplayName("자동 타입 결정 - LMS")
    void createAutoType_Lms() {
        // Given
        String longContent = "a".repeat(81); // SMS 최대 길이 초과
        
        // When
        SmsMessage message = SmsMessage.createAutoType(longContent);
        
        // Then
        assertEquals(MessageType.LMS, message.getType());
    }
    
    @Test
    @DisplayName("정적 팩토리 메소드 - createSms")
    void createSms() {
        // When
        SmsMessage sms = SmsMessage.createSms();
        
        // Then
        assertEquals(MessageType.SMS, sms.getType());
    }
    
    @Test
    @DisplayName("정적 팩토리 메소드 - createLms")
    void createLms() {
        // When
        SmsMessage lms = SmsMessage.createLms();
        
        // Then
        assertEquals(MessageType.LMS, lms.getType());
    }
    
    @Test
    @DisplayName("정적 팩토리 메소드 - createMms")
    void createMms() {
        // When
        SmsMessage mms = SmsMessage.createMms();
        
        // Then
        assertEquals(MessageType.MMS, mms.getType());
    }
    
    @Test
    @DisplayName("빌더 패턴 테스트")
    void builderPattern() {
        // When
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-010")
            .sender("TEST_SENDER")
            .senderNumber("01011111111")
            .receiver("01022222222")
            .content("빌더 테스트")
            .templateCode("TEMPLATE_TEST")
            .subject("테스트 제목")
            .build();
        
        // Then
        assertEquals("MSG-010", message.getMessageId());
        assertEquals("TEST_SENDER", message.getSender());
        assertEquals("01011111111", message.getSenderNumber());
        assertEquals("01022222222", message.getReceiver());
        assertEquals("빌더 테스트", message.getContent());
        assertEquals("TEMPLATE_TEST", message.getTemplateCode());
        assertEquals("테스트 제목", message.getSubject());
    }
    
    @Test
    @DisplayName("전화번호 하이픈 포함 유효성 검증")
    void isValid_PhoneNumberWithHyphen() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-011")
            .sender("TEST")
            .senderNumber("010-1234-5678")
            .receiver("010-8765-4321")
            .content("하이픈 포함 번호 테스트")
            .build();
        
        // When
        boolean isValid = message.isValid();
        
        // Then
        assertTrue(isValid);
    }
}