package com.bwc.messaging.sms.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.domain.SmsMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsRepositoryImpl 테스트")
class SmsRepositoryImplTest {

    @Mock
    private SmsMessageJpaRepository smsMessageJpaRepository;
    
    @InjectMocks
    private SmsRepositoryImpl smsRepositoryImpl;
    
    private SmsMessage testSmsMessage;
    private SmsMessageEntity testEntity;
    
    @BeforeEach
    void setUp() {
        testSmsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-001")
            .sender("TEST_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .templateCode("TEMPLATE_001")
            .build();
        testSmsMessage.setStatus(MessageStatus.PENDING);
        
        testEntity = SmsMessageEntity.builder()
            .messageId("MSG-001")
            .type(MessageType.SMS)
            .sender("TEST_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .status(MessageStatus.PENDING)
            .templateCode("TEMPLATE_001")
            .retryCount(0)
            .createdAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("SMS 메시지 저장 성공")
    void save_Success() {
        // Given
        when(smsMessageJpaRepository.save(any(SmsMessageEntity.class))).thenReturn(testEntity);
        
        // When
        assertDoesNotThrow(() -> smsRepositoryImpl.save(testSmsMessage));
        
        // Then
        verify(smsMessageJpaRepository).save(any(SmsMessageEntity.class));
    }
    
    @Test
    @DisplayName("메시지 ID로 조회 성공")
    void findById_Success() {
        // Given
        when(smsMessageJpaRepository.findByMessageId("MSG-001")).thenReturn(Optional.of(testEntity));
        
        // When
        Optional<SmsMessage> result = smsRepositoryImpl.findById("MSG-001");
        
        // Then
        assertTrue(result.isPresent());
        SmsMessage foundMessage = result.get();
        assertEquals("MSG-001", foundMessage.getMessageId());
        assertEquals("TEST_SERVICE", foundMessage.getSender());
        
        verify(smsMessageJpaRepository).findByMessageId("MSG-001");
    }
    
    @Test
    @DisplayName("메시지 ID로 조회 - 결과 없음")
    void findById_NotFound() {
        // Given
        when(smsMessageJpaRepository.findByMessageId("NON-EXISTENT")).thenReturn(Optional.empty());
        
        // When
        Optional<SmsMessage> result = smsRepositoryImpl.findById("NON-EXISTENT");
        
        // Then
        assertFalse(result.isPresent());
        verify(smsMessageJpaRepository).findByMessageId("NON-EXISTENT");
    }
    
    @Test
    @DisplayName("메시지 상태 업데이트 성공")
    void updateStatus_Success() {
        // Given
        when(smsMessageJpaRepository.findByMessageId("MSG-001")).thenReturn(Optional.of(testEntity));
        when(smsMessageJpaRepository.save(any(SmsMessageEntity.class))).thenReturn(testEntity);
        
        // When
        assertDoesNotThrow(() -> smsRepositoryImpl.updateStatus("MSG-001", MessageStatus.SENT));
        
        // Then
        verify(smsMessageJpaRepository).findByMessageId("MSG-001");
        verify(smsMessageJpaRepository).save(any(SmsMessageEntity.class));
    }
    
    @Test
    @DisplayName("재발송 횟수 증가 성공")
    void incrementRetryCount_Success() {
        // Given
        when(smsMessageJpaRepository.findByMessageId("MSG-001")).thenReturn(Optional.of(testEntity));
        when(smsMessageJpaRepository.save(any(SmsMessageEntity.class))).thenReturn(testEntity);
        
        // When
        assertDoesNotThrow(() -> smsRepositoryImpl.incrementRetryCount("MSG-001"));
        
        // Then
        verify(smsMessageJpaRepository).findByMessageId("MSG-001");
        verify(smsMessageJpaRepository).save(any(SmsMessageEntity.class));
    }
    
    @Test
    @DisplayName("대기 중인 메시지 조회")
    void findPendingMessages_Success() {
        // Given
        when(smsMessageJpaRepository.findByStatusOrderByCreatedAtAsc(MessageStatus.PENDING))
            .thenReturn(List.of(testEntity));
        
        // When
        List<SmsMessage> result = smsRepositoryImpl.findPendingMessages(10);
        
        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("MSG-001", result.get(0).getMessageId());
        
        verify(smsMessageJpaRepository).findByStatusOrderByCreatedAtAsc(MessageStatus.PENDING);
    }
    
    @Test
    @DisplayName("헬스 체크")
    void healthCheck_Success() {
        // Given
        when(smsMessageJpaRepository.count()).thenReturn(100L);
        
        // When & Then
        assertDoesNotThrow(() -> smsRepositoryImpl.healthCheck());
        verify(smsMessageJpaRepository).count();
    }
}