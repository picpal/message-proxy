package com.bwc.messaging.sms.application.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.application.strategy.impl.LguV1SmsStrategy;
import com.bwc.messaging.sms.application.strategy.impl.LguV2SmsStrategy;
import com.bwc.messaging.sms.application.strategy.impl.MtsSmsStrategy;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v1.LguV1MessageJpaRepository;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v2.LguV2MessageJpaRepository;
import com.bwc.messaging.sms.infrastructure.persistence.mts.MtsMessageJpaRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SMS 발송사별 전략 통합 테스트")
class SmsVendorStrategyIntegrationTest {

    @Autowired
    private LguV1SmsStrategy lguV1SmsStrategy;
    
    @Autowired
    private LguV2SmsStrategy lguV2SmsStrategy;
    
    @Autowired
    private MtsSmsStrategy mtsSmsStrategy;
    
    @Autowired
    private SmsStrategyFactory smsStrategyFactory;
    
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
            .messageId("STRATEGY-TEST-001")
            .sender("TEST_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("전략 테스트 메시지")
            .templateCode("TEMPLATE_001")
            .build();
    }
    
    @Test
    @DisplayName("LGU V1 전략으로 SMS 발송 및 DB 저장 테스트")
    void lguV1Strategy_sendAndStore_Success() {
        // Given
        testSmsMessage.setMessageId("LGU-V1-STRATEGY-001");
        
        // When
        MessageResult result = lguV1SmsStrategy.send(testSmsMessage);
        
        // Then - 결과 검증
        assertTrue(result.isSuccess());
        assertEquals("LGU-V1-STRATEGY-001", result.getMessageId());
        
        // DB 저장 검증
        var savedEntity = lguV1MessageJpaRepository.findByMessageId("LGU-V1-STRATEGY-001");
        assertTrue(savedEntity.isPresent());
        assertEquals(MessageStatus.PENDING, savedEntity.get().getStatus());
        assertEquals("TEST_SERVICE", savedEntity.get().getSender());
        assertEquals("전략 테스트 메시지", savedEntity.get().getContent());
        
        // 전략별 고유 필드 검증
        assertEquals("01012345678", savedEntity.get().getCallbackNumber());
        assertEquals("SMS", savedEntity.get().getServiceType());
        assertEquals("TEMPLATE_001", savedEntity.get().getTemplateId());
        assertEquals("N", savedEntity.get().getAdFlag());
        assertEquals("Y", savedEntity.get().getReportFlag());
    }
    
    @Test
    @DisplayName("LGU V2 전략으로 SMS 발송 및 DB 저장 테스트")
    void lguV2Strategy_sendAndStore_Success() {
        // Given
        testSmsMessage.setMessageId("LGU-V2-STRATEGY-001");
        
        // When
        MessageResult result = lguV2SmsStrategy.send(testSmsMessage);
        
        // Then - 결과 검증
        assertTrue(result.isSuccess());
        assertEquals("LGU-V2-STRATEGY-001", result.getMessageId());
        
        // DB 저장 검증
        var savedEntity = lguV2MessageJpaRepository.findByMessageId("LGU-V2-STRATEGY-001");
        assertTrue(savedEntity.isPresent());
        assertEquals(MessageStatus.PENDING, savedEntity.get().getStatus());
        assertEquals("전략 테스트 메시지", savedEntity.get().getContent());
        
        // LGU V2 전용 필드 검증
        assertEquals("2.0", savedEntity.get().getApiVersion());
        assertEquals("SMS", savedEntity.get().getContentType());
        assertEquals("NORMAL", savedEntity.get().getPriority());
        assertEquals(3600, savedEntity.get().getTtl());
        assertEquals("Y", savedEntity.get().getDeliveryReceipt());
        assertEquals(3, savedEntity.get().getMaxRetryCount());
    }
    
    @Test
    @DisplayName("MTS 전략으로 SMS 발송 및 DB 저장 테스트")
    void mtsStrategy_sendAndStore_Success() {
        // Given
        testSmsMessage.setMessageId("MTS-STRATEGY-001");
        
        // When
        MessageResult result = mtsSmsStrategy.send(testSmsMessage);
        
        // Then - 결과 검증
        assertTrue(result.isSuccess());
        assertEquals("MTS-STRATEGY-001", result.getMessageId());
        
        // DB 저장 검증
        var savedEntity = mtsMessageJpaRepository.findByMessageId("MTS-STRATEGY-001");
        assertTrue(savedEntity.isPresent());
        assertEquals(MessageStatus.PENDING, savedEntity.get().getStatus());
        assertEquals("전략 테스트 메시지", savedEntity.get().getContent());
        
        // MTS 전용 필드 검증
        assertEquals("01", savedEntity.get().getMessageCode()); // SMS
        assertEquals("01", savedEntity.get().getTelecomCode()); // SKT
        assertEquals("82", savedEntity.get().getNationCode());
        assertEquals("01087654321", savedEntity.get().getDestPhone());
        assertEquals("01012345678", savedEntity.get().getSendPhone());
        assertEquals("전략 테스트 메시지", savedEntity.get().getMsgBody());
        assertEquals("N", savedEntity.get().getReserveYn());
        assertEquals("Y", savedEntity.get().getReportYn());
        assertEquals("DEFAULT_COMPANY", savedEntity.get().getCompanyId());
        assertEquals("SMS", savedEntity.get().getServiceCd());
        assertEquals("01", savedEntity.get().getBillType());
    }
    
    @Test
    @DisplayName("팩토리를 통한 전략 선택 및 발송 테스트")
    void strategyFactory_selectAndSend_Success() {
        // Given
        SmsMessage lguV1Message = createTestMessage("FACTORY-LGU-V1", MessageType.SMS);
        SmsMessage lguV2Message = createTestMessage("FACTORY-LGU-V2", MessageType.LMS);
        SmsMessage mtsMessage = createTestMessage("FACTORY-MTS", MessageType.SMS);
        
        // When & Then - LGU V1
        SmsStrategy lguV1Strategy = smsStrategyFactory.getStrategy(MessageChannel.LGU_V1);
        assertNotNull(lguV1Strategy);
        assertEquals(MessageChannel.LGU_V1, lguV1Strategy.getSupportedChannel());
        
        MessageResult lguV1Result = lguV1Strategy.send(lguV1Message);
        assertTrue(lguV1Result.isSuccess());
        assertTrue(lguV1MessageJpaRepository.findByMessageId("FACTORY-LGU-V1").isPresent());
        
        // When & Then - LGU V2
        SmsStrategy lguV2Strategy = smsStrategyFactory.getStrategy(MessageChannel.LGU_V2);
        assertNotNull(lguV2Strategy);
        assertEquals(MessageChannel.LGU_V2, lguV2Strategy.getSupportedChannel());
        
        MessageResult lguV2Result = lguV2Strategy.send(lguV2Message);
        assertTrue(lguV2Result.isSuccess());
        assertTrue(lguV2MessageJpaRepository.findByMessageId("FACTORY-LGU-V2").isPresent());
        
        // When & Then - MTS
        SmsStrategy mtsStrategy = smsStrategyFactory.getStrategy(MessageChannel.MTS);
        assertNotNull(mtsStrategy);
        assertEquals(MessageChannel.MTS, mtsStrategy.getSupportedChannel());
        
        MessageResult mtsResult = mtsStrategy.send(mtsMessage);
        assertTrue(mtsResult.isSuccess());
        assertTrue(mtsMessageJpaRepository.findByMessageId("FACTORY-MTS").isPresent());
    }
    
    @Test
    @DisplayName("발송사별 상태 조회 테스트")
    void vendorStrategies_getStatus_Success() {
        // Given - 각 발송사에 메시지 저장
        testSmsMessage.setMessageId("STATUS-LGU-V1");
        lguV1SmsStrategy.send(testSmsMessage);
        
        testSmsMessage.setMessageId("STATUS-LGU-V2");
        lguV2SmsStrategy.send(testSmsMessage);
        
        testSmsMessage.setMessageId("STATUS-MTS");
        mtsSmsStrategy.send(testSmsMessage);
        
        // When & Then - 상태 조회
        assertEquals(MessageStatus.PENDING, lguV1SmsStrategy.getStatus("STATUS-LGU-V1"));
        assertEquals(MessageStatus.PENDING, lguV2SmsStrategy.getStatus("STATUS-LGU-V2"));
        assertEquals(MessageStatus.PENDING, mtsSmsStrategy.getStatus("STATUS-MTS"));
        
        // 존재하지 않는 메시지 조회
        assertEquals(MessageStatus.FAILED, lguV1SmsStrategy.getStatus("NON-EXISTENT"));
        assertEquals(MessageStatus.FAILED, lguV2SmsStrategy.getStatus("NON-EXISTENT"));
        assertEquals(MessageStatus.FAILED, mtsSmsStrategy.getStatus("NON-EXISTENT"));
    }
    
    @Test
    @DisplayName("발송사별 헬스 체크 테스트")
    void vendorStrategies_healthCheck_Success() {
        // When & Then
        assertTrue(lguV1SmsStrategy.isHealthy());
        assertTrue(lguV2SmsStrategy.isHealthy());
        assertTrue(mtsSmsStrategy.isHealthy());
    }
    
    @Test
    @DisplayName("발송사별 메시지 발송 가능 여부 테스트")
    void vendorStrategies_canSend_Success() {
        // Given - 유효한 메시지
        SmsMessage validMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("VALID-MESSAGE")
            .sender("TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("유효한 메시지")
            .build();
        
        // Given - 무효한 메시지
        SmsMessage invalidMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("INVALID-MESSAGE")
            .sender("TEST")
            .senderNumber("invalid")
            .receiver("invalid")
            .content("")
            .build();
        
        // When & Then - 유효한 메시지
        assertTrue(lguV1SmsStrategy.canSend(validMessage));
        assertTrue(lguV2SmsStrategy.canSend(validMessage));
        assertTrue(mtsSmsStrategy.canSend(validMessage));
        
        // When & Then - 무효한 메시지
        assertFalse(lguV1SmsStrategy.canSend(invalidMessage));
        assertFalse(lguV2SmsStrategy.canSend(invalidMessage));
        assertFalse(mtsSmsStrategy.canSend(invalidMessage));
    }
    
    @Test
    @DisplayName("팩토리를 통한 사용 가능한 채널 조회 테스트")
    void strategyFactory_getAvailableChannels_Success() {
        // When
        List<MessageChannel> availableChannels = smsStrategyFactory.getAvailableChannels();
        
        // Then
        assertNotNull(availableChannels);
        assertTrue(availableChannels.contains(MessageChannel.LGU_V1));
        assertTrue(availableChannels.contains(MessageChannel.LGU_V2));
        assertTrue(availableChannels.contains(MessageChannel.MTS));
        
        // 최소 3개의 SMS 채널이 사용 가능해야 함
        assertTrue(availableChannels.size() >= 3);
    }
    
    @Test
    @DisplayName("다양한 메시지 타입별 발송사 저장 테스트")
    void differentMessageTypes_saveToVendors_Success() {
        // Given
        SmsMessage smsMessage = createTestMessage("TYPE-SMS", MessageType.SMS);
        SmsMessage lmsMessage = createTestMessage("TYPE-LMS", MessageType.LMS);
        SmsMessage mmsMessage = createTestMessage("TYPE-MMS", MessageType.MMS);
        
        // When - 각 타입을 다른 발송사에 저장
        lguV1SmsStrategy.send(smsMessage);
        lguV2SmsStrategy.send(lmsMessage);
        mtsSmsStrategy.send(mmsMessage);
        
        // Then - 타입별 저장 확인
        var lguV1Entity = lguV1MessageJpaRepository.findByMessageId("TYPE-SMS");
        assertTrue(lguV1Entity.isPresent());
        assertEquals(MessageType.SMS, lguV1Entity.get().getMessageType());
        assertEquals("SMS", lguV1Entity.get().getServiceType());
        
        var lguV2Entity = lguV2MessageJpaRepository.findByMessageId("TYPE-LMS");
        assertTrue(lguV2Entity.isPresent());
        assertEquals(MessageType.LMS, lguV2Entity.get().getMessageType());
        assertEquals("LMS", lguV2Entity.get().getContentType());
        
        var mtsEntity = mtsMessageJpaRepository.findByMessageId("TYPE-MMS");
        assertTrue(mtsEntity.isPresent());
        assertEquals(MessageType.MMS, mtsEntity.get().getMessageType());
        assertEquals("03", mtsEntity.get().getMessageCode()); // MMS
    }
    
    private SmsMessage createTestMessage(String messageId, MessageType messageType) {
        return SmsMessage.builder(messageType)
            .messageId(messageId)
            .sender("TEST_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지 - " + messageType.name())
            .subject(messageType == MessageType.LMS || messageType == MessageType.MMS ? "테스트 제목" : null)
            .build();
    }
}