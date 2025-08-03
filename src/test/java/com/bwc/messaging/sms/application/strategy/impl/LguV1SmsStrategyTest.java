package com.bwc.messaging.sms.application.strategy.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("LguV1SmsStrategy 테스트")
class LguV1SmsStrategyTest {

    @Mock
    private SmsRepository smsRepository;
    
    @InjectMocks
    private LguV1SmsStrategy lguV1SmsStrategy;
    
    private SmsMessage validSmsMessage;
    
    @BeforeEach
    void setUp() {
        validSmsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-001")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();
    }
    
    @Test
    @DisplayName("SMS 발송 성공")
    void send_Success() {
        // When
        MessageResult result = lguV1SmsStrategy.send(validSmsMessage);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals("MSG-001", result.getMessageId());
        assertEquals("MSG-001", result.getVendorMessageId());
        
        // 메시지 상태 및 채널 설정 확인
        assertEquals(MessageChannel.LGU_V1, validSmsMessage.getChannel());
        assertEquals(MessageStatus.PENDING, validSmsMessage.getStatus());
        
        verify(smsRepository).save(validSmsMessage);
    }
    
    @Test
    @DisplayName("SMS 발송 실패 - 저장소 예외")
    void send_RepositoryException() {
        // Given
        doThrow(new RuntimeException("DB 연결 오류")).when(smsRepository).save(any(SmsMessage.class));
        
        // When
        MessageResult result = lguV1SmsStrategy.send(validSmsMessage);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals("MSG-001", result.getMessageId());
        assertEquals("DB_ERROR", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("DB 연결 오류"));
        
        verify(smsRepository).save(validSmsMessage);
    }
    
    @Test
    @DisplayName("SMS 상태 조회 성공")
    void getStatus_Success() {
        // Given
        validSmsMessage.setStatus(MessageStatus.SENT);
        when(smsRepository.findById("MSG-001")).thenReturn(Optional.of(validSmsMessage));
        
        // When
        MessageStatus status = lguV1SmsStrategy.getStatus("MSG-001");
        
        // Then
        assertEquals(MessageStatus.SENT, status);
        verify(smsRepository).findById("MSG-001");
    }
    
    @Test
    @DisplayName("SMS 상태 조회 - 메시지 없음")
    void getStatus_MessageNotFound() {
        // Given
        when(smsRepository.findById("MSG-999")).thenReturn(Optional.empty());
        
        // When
        MessageStatus status = lguV1SmsStrategy.getStatus("MSG-999");
        
        // Then
        assertEquals(MessageStatus.FAILED, status);
        verify(smsRepository).findById("MSG-999");
    }
    
    @Test
    @DisplayName("메시지 발송 가능 여부 - 유효한 메시지")
    void canSend_ValidMessage() {
        // Given
        doNothing().when(smsRepository).healthCheck();
        
        // When
        boolean canSend = lguV1SmsStrategy.canSend(validSmsMessage);
        
        // Then
        assertTrue(canSend);
    }
    
    @Test
    @DisplayName("메시지 발송 가능 여부 - 무효한 메시지")
    void canSend_InvalidMessage() {
        // Given
        SmsMessage invalidMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-002")
            .sender("TEST")
            .senderNumber("invalid")
            .receiver("invalid")
            .content("")
            .build();
        
        // When
        boolean canSend = lguV1SmsStrategy.canSend(invalidMessage);
        
        // Then
        assertFalse(canSend);
    }
    
    @Test
    @DisplayName("메시지 발송 가능 여부 - 전략 비정상")
    void canSend_StrategyUnhealthy() {
        // Given
        doThrow(new RuntimeException("Health check failed")).when(smsRepository).healthCheck();
        
        // When
        boolean canSend = lguV1SmsStrategy.canSend(validSmsMessage);
        
        // Then
        assertFalse(canSend);
    }
    
    @Test
    @DisplayName("지원하는 메시지 타입 확인")
    void getSupportedMessageType() {
        // When
        Class<SmsMessage> supportedType = lguV1SmsStrategy.getSupportedMessageType();
        
        // Then
        assertEquals(SmsMessage.class, supportedType);
    }
    
    @Test
    @DisplayName("지원하는 채널 확인")
    void getSupportedChannel() {
        // When
        MessageChannel supportedChannel = lguV1SmsStrategy.getSupportedChannel();
        
        // Then
        assertEquals(MessageChannel.LGU_V1, supportedChannel);
    }
    
    @Test
    @DisplayName("전략 상태 확인 - 정상")
    void isHealthy_Success() {
        // Given
        doNothing().when(smsRepository).healthCheck();
        
        // When
        boolean isHealthy = lguV1SmsStrategy.isHealthy();
        
        // Then
        assertTrue(isHealthy);
        verify(smsRepository).healthCheck();
    }
    
    @Test
    @DisplayName("전략 상태 확인 - 비정상")
    void isHealthy_Failed() {
        // Given
        doThrow(new RuntimeException("Health check failed")).when(smsRepository).healthCheck();
        
        // When
        boolean isHealthy = lguV1SmsStrategy.isHealthy();
        
        // Then
        assertFalse(isHealthy);
        verify(smsRepository).healthCheck();
    }
}