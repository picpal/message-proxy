package com.bwc.messaging.sms.infrastructure.persistence;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.config.TestDatabaseConfig;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v1.LguV1MessageEntity;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v1.LguV1MessageJpaRepository;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v2.LguV2MessageEntity;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v2.LguV2MessageJpaRepository;
import com.bwc.messaging.sms.infrastructure.persistence.mts.MtsMessageEntity;
import com.bwc.messaging.sms.infrastructure.persistence.mts.MtsMessageJpaRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")

@DisplayName("SMS 발송사별 Repository 통합 테스트")
class SmsVendorRepositoryIntegrationTest {

    
    
    @Autowired
    @Qualifier("lguV1SmsRepository")
    private SmsRepository lguV1SmsRepository;
    
    @Autowired
    @Qualifier("lguV2SmsRepository")
    private SmsRepository lguV2SmsRepository;
    
    @Autowired
    @Qualifier("mtsSmsRepository")
    private SmsRepository mtsSmsRepository;
    
    @Autowired
    private LguV1MessageJpaRepository lguV1MessageJpaRepository;
    
    @Autowired
    private LguV2MessageJpaRepository lguV2MessageJpaRepository;
    
    @Autowired
    private MtsMessageJpaRepository mtsMessageJpaRepository;
    
    private SmsMessage testSmsMessage;
    
    @BeforeEach
    void setUp() {
        testSmsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("TEST-MSG-001")
            .sender("TEST_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 SMS 메시지")
            .templateCode("TEMPLATE_001")
            .build();
        testSmsMessage.setStatus(MessageStatus.PENDING);
    }
    
    @Test
    @DisplayName("LGU V1 테이블에 SMS 메시지 저장 및 조회")
    void lguV1_saveAndFind_Success() {
        // Given
        testSmsMessage.setMessageId("LGU-V1-001");
        
        // When - 저장
        lguV1SmsRepository.save(testSmsMessage);

        
        // Then - DB에 실제 저장 확인
        Optional<LguV1MessageEntity> savedEntity = lguV1MessageJpaRepository
            .findByMessageId("LGU-V1-001");
        
        assertTrue(savedEntity.isPresent());
        assertEquals("LGU-V1-001", savedEntity.get().getMessageId());
        assertEquals(MessageType.SMS, savedEntity.get().getMessageType());
        assertEquals("TEST_SERVICE", savedEntity.get().getSender());
        assertEquals("01012345678", savedEntity.get().getSenderNumber());
        assertEquals("01087654321", savedEntity.get().getReceiver());
        assertEquals("테스트 SMS 메시지", savedEntity.get().getContent());
        assertEquals(MessageStatus.PENDING, savedEntity.get().getStatus());
        assertEquals("TEMPLATE_001", savedEntity.get().getTemplateId());
        
        // When - 조회
        Optional<SmsMessage> foundMessage = lguV1SmsRepository.findById("LGU-V1-001");
        
        // Then - 도메인 객체로 정상 변환 확인
        assertTrue(foundMessage.isPresent());
        assertEquals("LGU-V1-001", foundMessage.get().getMessageId());
        assertEquals(MessageStatus.PENDING, foundMessage.get().getStatus());
    }
    
    @Test
    @DisplayName("LGU V2 테이블에 SMS 메시지 저장 및 조회")
    void lguV2_saveAndFind_Success() {
        // Given
        testSmsMessage.setMessageId("LGU-V2-001");
        
        // When - 저장
        lguV2SmsRepository.save(testSmsMessage);

        
        // Then - DB에 실제 저장 확인
        Optional<LguV2MessageEntity> savedEntity = lguV2MessageJpaRepository
            .findByMessageId("LGU-V2-001");
        
        assertTrue(savedEntity.isPresent());
        assertEquals("LGU-V2-001", savedEntity.get().getMessageId());
        assertEquals(MessageType.SMS, savedEntity.get().getMessageType());
        assertEquals("SMS", savedEntity.get().getContentType());
        assertEquals("2.0", savedEntity.get().getApiVersion());
        assertEquals("NORMAL", savedEntity.get().getPriority());
        assertEquals(3600, savedEntity.get().getTtl());
        assertEquals("Y", savedEntity.get().getDeliveryReceipt());
        
        // When - 조회
        Optional<SmsMessage> foundMessage = lguV2SmsRepository.findById("LGU-V2-001");
        
        // Then
        assertTrue(foundMessage.isPresent());
        assertEquals("LGU-V2-001", foundMessage.get().getMessageId());
    }
    
    @Test
    @DisplayName("MTS 테이블에 SMS 메시지 저장 및 조회")
    void mts_saveAndFind_Success() {
        // Given
        testSmsMessage.setMessageId("MTS-001");
        
        // When - 저장
        mtsSmsRepository.save(testSmsMessage);

        
        // Then - DB에 실제 저장 확인
        Optional<MtsMessageEntity> savedEntity = mtsMessageJpaRepository
            .findByMessageId("MTS-001");
        
        assertTrue(savedEntity.isPresent());
        assertEquals("MTS-001", savedEntity.get().getMessageId());
        assertEquals(MessageType.SMS, savedEntity.get().getMessageType());
        assertEquals("01", savedEntity.get().getMessageCode()); // SMS
        assertEquals("01", savedEntity.get().getTelecomCode()); // SKT (010으로 시작)
        assertEquals("82", savedEntity.get().getNationCode());
        assertEquals("01087654321", savedEntity.get().getDestPhone());
        assertEquals("01012345678", savedEntity.get().getSendPhone());
        assertEquals("N", savedEntity.get().getReserveYn());
        assertEquals("Y", savedEntity.get().getReportYn());
        assertEquals("DEFAULT_COMPANY", savedEntity.get().getCompanyId());
        assertEquals("SMS", savedEntity.get().getServiceCd());
        
        // When - 조회
        Optional<SmsMessage> foundMessage = mtsSmsRepository.findById("MTS-001");
        
        // Then
        assertTrue(foundMessage.isPresent());
        assertEquals("MTS-001", foundMessage.get().getMessageId());
    }
    
    @Test
    @DisplayName("발송사별 상태 업데이트 테스트")
    void updateStatus_AllVendors_Success() {
        // Given - 각 발송사에 메시지 저장
        SmsMessage lguV1Message = createTestMessage("LGU-V1-STATUS", MessageType.SMS);
        SmsMessage lguV2Message = createTestMessage("LGU-V2-STATUS", MessageType.LMS);
        SmsMessage mtsMessage = createTestMessage("MTS-STATUS", MessageType.SMS);
        
        lguV1SmsRepository.save(lguV1Message);
        lguV2SmsRepository.save(lguV2Message);
        mtsSmsRepository.save(mtsMessage);

        
        // When - 상태 업데이트
        lguV1SmsRepository.updateStatus("LGU-V1-STATUS", MessageStatus.SENT);
        lguV2SmsRepository.updateStatus("LGU-V2-STATUS", MessageStatus.DELIVERED);
        mtsSmsRepository.updateStatus("MTS-STATUS", MessageStatus.FAILED);

        
        // Then - 각 테이블별 상태 확인
        Optional<LguV1MessageEntity> lguV1Entity = lguV1MessageJpaRepository
            .findByMessageId("LGU-V1-STATUS");
        assertTrue(lguV1Entity.isPresent());
        assertEquals(MessageStatus.SENT, lguV1Entity.get().getStatus());
        assertNotNull(lguV1Entity.get().getSentAt());
        assertNotNull(lguV1Entity.get().getUpdatedAt());
        
        Optional<LguV2MessageEntity> lguV2Entity = lguV2MessageJpaRepository
            .findByMessageId("LGU-V2-STATUS");
        assertTrue(lguV2Entity.isPresent());
        assertEquals(MessageStatus.DELIVERED, lguV2Entity.get().getStatus());
        assertNotNull(lguV2Entity.get().getDeliveredAt());
        
        Optional<MtsMessageEntity> mtsEntity = mtsMessageJpaRepository
            .findByMessageId("MTS-STATUS");
        assertTrue(mtsEntity.isPresent());
        assertEquals(MessageStatus.FAILED, mtsEntity.get().getStatus());
    }
    
    @Test
    @DisplayName("발송사별 재시도 횟수 증가 테스트")
    void incrementRetryCount_AllVendors_Success() {
        // Given
        SmsMessage lguV1Message = createTestMessage("LGU-V1-RETRY", MessageType.SMS);
        SmsMessage lguV2Message = createTestMessage("LGU-V2-RETRY", MessageType.SMS);
        SmsMessage mtsMessage = createTestMessage("MTS-RETRY", MessageType.SMS);
        
        lguV1SmsRepository.save(lguV1Message);
        lguV2SmsRepository.save(lguV2Message);
        mtsSmsRepository.save(mtsMessage);

        
        // When - 재시도 횟수 증가
        lguV1SmsRepository.incrementRetryCount("LGU-V1-RETRY");
        lguV2SmsRepository.incrementRetryCount("LGU-V2-RETRY");
        mtsSmsRepository.incrementRetryCount("MTS-RETRY");

        
        // Then
        Optional<LguV1MessageEntity> lguV1Entity = lguV1MessageJpaRepository
            .findByMessageId("LGU-V1-RETRY");
        assertTrue(lguV1Entity.isPresent());
        assertEquals(1, lguV1Entity.get().getRetryCount());
        
        Optional<LguV2MessageEntity> lguV2Entity = lguV2MessageJpaRepository
            .findByMessageId("LGU-V2-RETRY");
        assertTrue(lguV2Entity.isPresent());
        assertEquals(1, lguV2Entity.get().getRetryCount());
        
        Optional<MtsMessageEntity> mtsEntity = mtsMessageJpaRepository
            .findByMessageId("MTS-RETRY");
        assertTrue(mtsEntity.isPresent());
        assertEquals(1, mtsEntity.get().getRetryCount());
    }
    
    @Test
    @DisplayName("발송사별 대기 메시지 조회 테스트")
    void findPendingMessages_AllVendors_Success() {
        // Given - 여러 상태의 메시지들 저장
        savePendingMessage("LGU-V1-PENDING-1", lguV1SmsRepository);
        savePendingMessage("LGU-V1-PENDING-2", lguV1SmsRepository);
        saveSentMessage("LGU-V1-SENT-1", lguV1SmsRepository);
        
        savePendingMessage("LGU-V2-PENDING-1", lguV2SmsRepository);
        savePendingMessage("MTS-PENDING-1", mtsSmsRepository);

        
        // When - 대기 메시지 조회
        List<SmsMessage> lguV1PendingMessages = lguV1SmsRepository.findPendingMessages(10);
        List<SmsMessage> lguV2PendingMessages = lguV2SmsRepository.findPendingMessages(10);
        List<SmsMessage> mtsPendingMessages = mtsSmsRepository.findPendingMessages(10);
        
        // Then
        assertEquals(2, lguV1PendingMessages.size());
        assertEquals(1, lguV2PendingMessages.size());
        assertEquals(1, mtsPendingMessages.size());
        
        assertTrue(lguV1PendingMessages.stream()
            .allMatch(msg -> msg.getStatus() == MessageStatus.PENDING));
        assertTrue(lguV2PendingMessages.stream()
            .allMatch(msg -> msg.getStatus() == MessageStatus.PENDING));
        assertTrue(mtsPendingMessages.stream()
            .allMatch(msg -> msg.getStatus() == MessageStatus.PENDING));
    }
    
    @Test
    @DisplayName("발송사별 헬스 체크 테스트")
    void healthCheck_AllVendors_Success() {
        // When & Then - 예외 발생하지 않으면 성공
        assertDoesNotThrow(() -> lguV1SmsRepository.healthCheck());
        assertDoesNotThrow(() -> lguV2SmsRepository.healthCheck());
        assertDoesNotThrow(() -> mtsSmsRepository.healthCheck());
    }
    
    private SmsMessage createTestMessage(String messageId, MessageType messageType) {
        SmsMessage message = SmsMessage.builder(messageType)
            .messageId(messageId)
            .sender("TEST_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();
        message.setStatus(MessageStatus.PENDING);
        return message;
    }
    
    private void savePendingMessage(String messageId, SmsRepository repository) {
        SmsMessage message = createTestMessage(messageId, MessageType.SMS);
        message.setStatus(MessageStatus.PENDING);
        repository.save(message);
    }
    
    private void saveSentMessage(String messageId, SmsRepository repository) {
        SmsMessage message = createTestMessage(messageId, MessageType.SMS);
        message.setStatus(MessageStatus.SENT);
        repository.save(message);
    }
}