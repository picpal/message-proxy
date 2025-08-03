package com.bwc.messaging.sms.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.bwc.messaging.sms.application.strategy.SmsStrategy;
import com.bwc.messaging.sms.application.strategy.SmsStrategyFactory;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsApplicationService 테스트")
class SmsApplicationServiceTest {

    @Mock
    private SmsRepository smsRepository;
    
    @Mock
    private SmsStrategyFactory strategyFactory;
    
    @Mock
    private SmsChannelRouter channelRouter;
    
    @Mock
    private SmsStrategy smsStrategy;
    
    @InjectMocks
    private SmsApplicationService smsApplicationService;
    
    private SmsMessage validSmsMessage;
    private SmsMessage invalidSmsMessage;
    
    @BeforeEach
    void setUp() {
        validSmsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-001")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();
            
        invalidSmsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-002")
            .sender("TEST")
            .senderNumber("invalid")
            .receiver("invalid")
            .content("")
            .build();
    }
    
    @Test
    @DisplayName("SMS 발송 성공 테스트")
    void sendSms_Success() {
        // Given
        when(channelRouter.selectChannel(validSmsMessage)).thenReturn(MessageChannel.LGU_V1);
        when(strategyFactory.getStrategy(MessageChannel.LGU_V1)).thenReturn(smsStrategy);
        when(smsStrategy.send(validSmsMessage)).thenReturn(MessageResult.success("MSG-001", "LGU-12345"));
        
        // When
        MessageResult result = smsApplicationService.sendSms(validSmsMessage);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals("MSG-001", result.getMessageId());
        
        verify(smsRepository).save(validSmsMessage);
        verify(smsRepository).updateStatus("MSG-001", MessageStatus.SENT);
        verify(channelRouter).selectChannel(validSmsMessage);
        verify(strategyFactory).getStrategy(MessageChannel.LGU_V1);
        verify(smsStrategy).send(validSmsMessage);
    }
    
    @Test
    @DisplayName("SMS 발송 실패 - 유효하지 않은 메시지")
    void sendSms_InvalidMessage() {
        // When
        MessageResult result = smsApplicationService.sendSms(invalidSmsMessage);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals("MSG-002", result.getMessageId());
        assertEquals("INVALID_MESSAGE", result.getErrorCode());
        assertEquals("Invalid message format", result.getErrorMessage());
        
        verify(smsRepository, never()).save(any());
        verify(channelRouter, never()).selectChannel(any());
        verify(strategyFactory, never()).getStrategy(any());
    }
    
    @Test
    @DisplayName("SMS 발송 실패 - 저장소 예외")
    void sendSms_RepositoryException() {
        // Given
        doThrow(new RuntimeException("DB 연결 오류")).when(smsRepository).save(validSmsMessage);
        
        // When
        MessageResult result = smsApplicationService.sendSms(validSmsMessage);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals("MSG-001", result.getMessageId());
        assertEquals("SEND_ERROR", result.getErrorCode());
        
        verify(smsRepository).save(validSmsMessage);
        verify(smsRepository).updateStatus("MSG-001", MessageStatus.FAILED);
    }
    
    @Test
    @DisplayName("SMS 발송 실패 - 전략 실행 예외")
    void sendSms_StrategyException() {
        // Given
        when(channelRouter.selectChannel(validSmsMessage)).thenReturn(MessageChannel.LGU_V1);
        when(strategyFactory.getStrategy(MessageChannel.LGU_V1)).thenReturn(smsStrategy);
        when(smsStrategy.send(validSmsMessage)).thenThrow(new RuntimeException("API 호출 실패"));
        
        // When
        MessageResult result = smsApplicationService.sendSms(validSmsMessage);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals("MSG-001", result.getMessageId());
        assertEquals("SEND_ERROR", result.getErrorCode());
        
        verify(smsRepository).save(validSmsMessage);
        verify(smsRepository).updateStatus("MSG-001", MessageStatus.FAILED);
    }
    
    @Test
    @DisplayName("SMS 상태 조회 성공")
    void getSmsStatus_Success() {
        // Given
        validSmsMessage.setStatus(MessageStatus.SENT);
        when(smsRepository.findById("MSG-001")).thenReturn(Optional.of(validSmsMessage));
        
        // When
        MessageStatus status = smsApplicationService.getSmsStatus("MSG-001");
        
        // Then
        assertEquals(MessageStatus.SENT, status);
        verify(smsRepository).findById("MSG-001");
    }
    
    @Test
    @DisplayName("SMS 상태 조회 - 메시지 없음")
    void getSmsStatus_NotFound() {
        // Given
        when(smsRepository.findById("MSG-999")).thenReturn(Optional.empty());
        
        // When
        MessageStatus status = smsApplicationService.getSmsStatus("MSG-999");
        
        // Then
        assertEquals(MessageStatus.FAILED, status);
        verify(smsRepository).findById("MSG-999");
    }
    
    @Test
    @DisplayName("SMS 재발송 성공")
    void retrySms_Success() {
        // Given
        when(smsRepository.findById("MSG-001")).thenReturn(Optional.of(validSmsMessage));
        when(channelRouter.selectChannel(validSmsMessage)).thenReturn(MessageChannel.LGU_V1);
        when(strategyFactory.getStrategy(MessageChannel.LGU_V1)).thenReturn(smsStrategy);
        when(smsStrategy.send(validSmsMessage)).thenReturn(MessageResult.success("MSG-001", "LGU-12345"));
        
        // When
        smsApplicationService.retrySms("MSG-001");
        
        // Then
        verify(smsRepository).findById("MSG-001");
        verify(smsRepository).incrementRetryCount("MSG-001");
        verify(smsRepository, times(2)).save(validSmsMessage); // 재발송에서 한 번, 원본 save에서 한 번
    }
    
    @Test
    @DisplayName("SMS 재발송 - 메시지 없음")
    void retrySms_NotFound() {
        // Given
        when(smsRepository.findById("MSG-999")).thenReturn(Optional.empty());
        
        // When
        smsApplicationService.retrySms("MSG-999");
        
        // Then
        verify(smsRepository).findById("MSG-999");
        verify(smsRepository, never()).incrementRetryCount(any());
        verify(channelRouter, never()).selectChannel(any());
    }
}