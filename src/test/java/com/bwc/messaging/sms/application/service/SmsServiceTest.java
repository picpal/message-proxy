package com.bwc.messaging.sms.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.application.port.in.*;
import com.bwc.messaging.sms.application.port.out.SmsRepositoryPort;
import com.bwc.messaging.sms.application.port.out.SmsVendorPort;
import com.bwc.messaging.sms.domain.SmsMessage;

import java.util.Optional;

/**
 * 헥사고날 아키텍처가 적용된 SmsService 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SmsService 헥사고날 아키텍처 테스트")
class SmsServiceTest {
    
    @Mock
    private SmsRepositoryPort smsRepositoryPort;
    
    @Mock
    private SmsVendorPort smsVendorPort;
    
    private SmsService smsService;
    
    @BeforeEach
    void setUp() {
        smsService = new SmsService(smsRepositoryPort, smsVendorPort);
    }
    
    @Test
    @DisplayName("SMS 발송 Use Case 테스트")
    void testSendSmsUseCase() {
        // Given
        SendSmsCommand command = SendSmsCommand.builder()
            .messageId("test-001")
            .type(MessageType.SMS)
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("헥사고날 아키텍처 테스트 메시지")
            .build();
        
        MessageResult expectedResult = MessageResult.success("test-001", "vendor-001");
        
        when(smsVendorPort.send(any(SmsMessage.class))).thenReturn(expectedResult);
        
        // When
        MessageResult result = smsService.sendSms(command);
        
        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessageId()).isEqualTo("test-001");
        assertThat(result.getStatus()).isEqualTo(MessageStatus.SENT);
        
        // 포트 호출 검증
        verify(smsRepositoryPort).save(any(SmsMessage.class));
        verify(smsVendorPort).send(any(SmsMessage.class));
        verify(smsRepositoryPort).updateStatus("test-001", MessageStatus.SENT);
    }
    
    @Test
    @DisplayName("SMS 상태 조회 Use Case 테스트")
    void testGetSmsStatusUseCase() {
        // Given
        String messageId = "test-002";
        SmsMessage smsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId(messageId)
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("상태 조회 테스트")
            .build();
        smsMessage.setStatus(MessageStatus.SENT);
        
        when(smsRepositoryPort.findById(messageId)).thenReturn(Optional.of(smsMessage));
        
        GetSmsStatusQuery query = GetSmsStatusQuery.builder()
            .messageId(messageId)
            .build();
        
        // When
        MessageStatus status = smsService.getSmsStatus(query);
        
        // Then
        assertThat(status).isEqualTo(MessageStatus.SENT);
        verify(smsRepositoryPort).findById(messageId);
    }
    
    @Test
    @DisplayName("SMS 재전송 Use Case 테스트")
    void testRetrySmsUseCase() {
        // Given
        String messageId = "test-003";
        SmsMessage smsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId(messageId)
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("재전송 테스트")
            .build();
        
        when(smsRepositoryPort.findById(messageId)).thenReturn(Optional.of(smsMessage));
        when(smsVendorPort.send(any(SmsMessage.class))).thenReturn(
            MessageResult.success(messageId, "vendor-003"));
        
        RetrySmsCommand command = RetrySmsCommand.builder()
            .messageId(messageId)
            .build();
        
        // When
        smsService.retrySms(command);
        
        // Then
        verify(smsRepositoryPort).findById(messageId);
        verify(smsRepositoryPort).incrementRetryCount(messageId);
        verify(smsVendorPort).send(any(SmsMessage.class));
    }
    
    @Test
    @DisplayName("잘못된 메시지로 인한 발송 실패 테스트")
    void testSendSmsWithInvalidMessage() {
        // Given - 잘못된 수신자 번호
        SendSmsCommand command = SendSmsCommand.builder()
            .messageId("test-004")
            .type(MessageType.SMS)
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("invalid-number")
            .content("테스트 메시지")
            .build();
        
        // When
        MessageResult result = smsService.sendSms(command);
        
        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("INVALID_MESSAGE");
        
        // 실패 시에는 vendor로 전송되지 않아야 함
        verify(smsVendorPort, never()).send(any(SmsMessage.class));
    }
    
    @Test
    @DisplayName("메시지 타입 자동 결정 테스트 (SMS → LMS)")
    void testAutoTypeConversion() {
        // Given - 80자 초과 메시지
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longContent.append("긴메시지");
        }
        
        SendSmsCommand command = SendSmsCommand.builder()
            .messageId("test-005")
            .type(MessageType.SMS)  // SMS로 시작
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent.toString())
            .build();
        
        when(smsVendorPort.send(any(SmsMessage.class))).thenReturn(
            MessageResult.success("test-005", "vendor-005"));
        
        // When
        MessageResult result = smsService.sendSms(command);
        
        // Then
        assertThat(result.isSuccess()).isTrue();
        
        // 저장된 메시지가 LMS로 변경되었는지 검증
        verify(smsRepositoryPort).save(argThat(message -> 
            message.getType() == MessageType.LMS
        ));
    }
}