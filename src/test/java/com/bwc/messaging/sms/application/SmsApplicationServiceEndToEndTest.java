package com.bwc.messaging.sms.application;

import static org.junit.jupiter.api.Assertions.*;

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
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v1.LguV1MessageJpaRepository;
import com.bwc.messaging.sms.infrastructure.persistence.lgu.v2.LguV2MessageJpaRepository;
import com.bwc.messaging.sms.infrastructure.persistence.mts.MtsMessageJpaRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SMS 애플리케이션 서비스 End-to-End 테스트")
class SmsApplicationServiceEndToEndTest {

    @Autowired
    private SmsApplicationService smsApplicationService;
    
    @Autowired
    private SmsChannelRouter smsChannelRouter;
    
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
            .messageId("E2E-TEST-001")
            .sender("LGU_TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("End-to-End 테스트 메시지")
            .templateCode("TEMPLATE_E2E")
            .build();
    }
    
    @Test
    @DisplayName("LGU 서비스 코드 메시지 → LGU V1 테이블 저장 E2E 테스트")
    void lguServiceCode_routeToLguV1_endToEndSuccess() {
        // Given - LGU 서비스 코드
        testSmsMessage.setSender("LGU_TEST_SERVICE");
        testSmsMessage.setMessageId("E2E-LGU-V1-001");
        testSmsMessage.setContent("짧은 메시지"); // 80자 이하로 LGU V1 선택
        
        // When - 애플리케이션 서비스를 통한 SMS 발송
        MessageResult result = smsApplicationService.sendSms(testSmsMessage);
        
        // Then - 결과 검증
        assertTrue(result.isSuccess());
        assertEquals("E2E-LGU-V1-001", result.getMessageId());
        
        // 채널 라우팅 검증
        MessageChannel selectedChannel = smsChannelRouter.selectChannel(testSmsMessage);
        assertEquals(MessageChannel.LGU_V1, selectedChannel);
        
        // LGU V1 테이블에 저장 확인
        var lguV1Entity = lguV1MessageJpaRepository.findByMessageId("E2E-LGU-V1-001");
        assertTrue(lguV1Entity.isPresent());
        assertEquals(MessageStatus.PENDING, lguV1Entity.get().getStatus());
        assertEquals("LGU_TEST_SERVICE", lguV1Entity.get().getSender());
        assertEquals("짧은 메시지", lguV1Entity.get().getContent());
        assertEquals("01012345678", lguV1Entity.get().getCallbackNumber());
        assertEquals("SMS", lguV1Entity.get().getServiceType());
        assertEquals("TEMPLATE_E2E", lguV1Entity.get().getTemplateId());
        
        // 다른 테이블에는 저장되지 않음 확인
        assertFalse(lguV2MessageJpaRepository.findByMessageId("E2E-LGU-V1-001").isPresent());
        assertFalse(mtsMessageJpaRepository.findByMessageId("E2E-LGU-V1-001").isPresent());
    }
    
    @Test
    @DisplayName("LGU 긴 메시지 → LGU V2 테이블 저장 E2E 테스트")
    void lguLongMessage_routeToLguV2_endToEndSuccess() {
        // Given - LGU 서비스 코드 + 긴 메시지
        testSmsMessage.setSender("LGU_SERVICE");
        testSmsMessage.setMessageId("E2E-LGU-V2-001");
        testSmsMessage.setContent("이것은 매우 긴 메시지입니다. ".repeat(50)); // 1000자 이상으로 LGU V2 선택
        
        // When
        MessageResult result = smsApplicationService.sendSms(testSmsMessage);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals("E2E-LGU-V2-001", result.getMessageId());
        
        // 채널 라우팅 검증
        MessageChannel selectedChannel = smsChannelRouter.selectChannel(testSmsMessage);
        assertEquals(MessageChannel.LGU_V2, selectedChannel);
        
        // LGU V2 테이블에 저장 확인
        var lguV2Entity = lguV2MessageJpaRepository.findByMessageId("E2E-LGU-V2-001");
        assertTrue(lguV2Entity.isPresent());
        assertEquals(MessageStatus.PENDING, lguV2Entity.get().getStatus());
        assertEquals("LGU_SERVICE", lguV2Entity.get().getSender());
        assertTrue(lguV2Entity.get().getContent().length() > 1000);
        assertEquals("2.0", lguV2Entity.get().getApiVersion());
        assertEquals("LMS", lguV2Entity.get().getContentType()); // 긴 메시지로 LMS 타입
        
        // 다른 테이블에는 저장되지 않음 확인
        assertFalse(lguV1MessageJpaRepository.findByMessageId("E2E-LGU-V2-001").isPresent());
        assertFalse(mtsMessageJpaRepository.findByMessageId("E2E-LGU-V2-001").isPresent());
    }
    
    @Test
    @DisplayName("MTS 서비스 코드 → MTS 테이블 저장 E2E 테스트")
    void mtsServiceCode_routeToMts_endToEndSuccess() {
        // Given - MTS 서비스 코드
        testSmsMessage.setSender("MTS_TEST_SERVICE");
        testSmsMessage.setMessageId("E2E-MTS-001");
        testSmsMessage.setContent("MTS 테스트 메시지");
        testSmsMessage.setReceiver("01987654321"); // 018로 시작하는 LGU+ 번호
        
        // When
        MessageResult result = smsApplicationService.sendSms(testSmsMessage);
        
        // Then
        assertTrue(result.isSuccess());
        assertEquals("E2E-MTS-001", result.getMessageId());
        
        // 채널 라우팅 검증
        MessageChannel selectedChannel = smsChannelRouter.selectChannel(testSmsMessage);
        assertEquals(MessageChannel.MTS, selectedChannel);
        
        // MTS 테이블에 저장 확인
        var mtsEntity = mtsMessageJpaRepository.findByMessageId("E2E-MTS-001");
        assertTrue(mtsEntity.isPresent());
        assertEquals(MessageStatus.PENDING, mtsEntity.get().getStatus());
        assertEquals("MTS_TEST_SERVICE", mtsEntity.get().getSender());
        assertEquals("MTS 테스트 메시지", mtsEntity.get().getContent());
        assertEquals("01", mtsEntity.get().getMessageCode()); // SMS
        assertEquals("03", mtsEntity.get().getTelecomCode()); // LGU+ (018로 시작)
        assertEquals("01987654321", mtsEntity.get().getDestPhone());
        assertEquals("01012345678", mtsEntity.get().getSendPhone());
        assertEquals("DEFAULT_COMPANY", mtsEntity.get().getCompanyId());
        
        // 다른 테이블에는 저장되지 않음 확인
        assertFalse(lguV1MessageJpaRepository.findByMessageId("E2E-MTS-001").isPresent());
        assertFalse(lguV2MessageJpaRepository.findByMessageId("E2E-MTS-001").isPresent());
    }
    
    @Test
    @DisplayName("기본 서비스 코드 → 기본 채널(LGU V1) 저장 E2E 테스트")
    void defaultServiceCode_routeToDefault_endToEndSuccess() {
        // Given - 기본 서비스 코드
        testSmsMessage.setSender("OTHER_SERVICE");
        testSmsMessage.setMessageId("E2E-DEFAULT-001");
        testSmsMessage.setContent("기본 채널 테스트");
        
        // When
        MessageResult result = smsApplicationService.sendSms(testSmsMessage);
        
        // Then
        assertTrue(result.isSuccess());
        
        // 기본 채널(LGU V1) 선택 확인
        MessageChannel selectedChannel = smsChannelRouter.selectChannel(testSmsMessage);
        assertEquals(MessageChannel.LGU_V1, selectedChannel);
        
        // LGU V1 테이블에 저장 확인
        var lguV1Entity = lguV1MessageJpaRepository.findByMessageId("E2E-DEFAULT-001");
        assertTrue(lguV1Entity.isPresent());
        assertEquals("OTHER_SERVICE", lguV1Entity.get().getSender());
    }
    
    @Test
    @DisplayName("상태 조회 E2E 테스트")
    void getSmsStatus_endToEndSuccess() {
        // Given - 메시지 발송
        testSmsMessage.setMessageId("E2E-STATUS-001");
        testSmsMessage.setSender("LGU_SERVICE");
        smsApplicationService.sendSms(testSmsMessage);
        
        // When - 상태 조회
        MessageStatus status = smsApplicationService.getSmsStatus("E2E-STATUS-001");
        
        // Then
        assertEquals(MessageStatus.PENDING, status);
        
        // 존재하지 않는 메시지 조회
        MessageStatus notFoundStatus = smsApplicationService.getSmsStatus("NON-EXISTENT");
        assertEquals(MessageStatus.FAILED, notFoundStatus);
    }
    
    @Test
    @DisplayName("재발송 E2E 테스트")
    void retrySms_endToEndSuccess() {
        // Given - 메시지 발송
        testSmsMessage.setMessageId("E2E-RETRY-001");
        testSmsMessage.setSender("LGU_SERVICE");
        smsApplicationService.sendSms(testSmsMessage);
        
        // When - 재발송
        smsApplicationService.retrySms("E2E-RETRY-001");
        
        // Then - 재시도 횟수 증가 확인
        var lguV1Entity = lguV1MessageJpaRepository.findByMessageId("E2E-RETRY-001");
        assertTrue(lguV1Entity.isPresent());
        assertEquals(1, lguV1Entity.get().getRetryCount());
    }
    
    @Test
    @DisplayName("유효하지 않은 메시지 E2E 테스트")
    void invalidMessage_endToEndFailure() {
        // Given - 유효하지 않은 메시지
        SmsMessage invalidMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("E2E-INVALID-001")
            .sender("TEST")
            .senderNumber("invalid-number")
            .receiver("invalid-receiver")
            .content("") // 빈 내용
            .build();
        
        // When
        MessageResult result = smsApplicationService.sendSms(invalidMessage);
        
        // Then
        assertFalse(result.isSuccess());
        assertEquals("E2E-INVALID-001", result.getMessageId());
        assertEquals("INVALID_MESSAGE", result.getErrorCode());
        assertEquals("Invalid message format", result.getErrorMessage());
        
        // 어떤 테이블에도 저장되지 않음 확인
        assertFalse(lguV1MessageJpaRepository.findByMessageId("E2E-INVALID-001").isPresent());
        assertFalse(lguV2MessageJpaRepository.findByMessageId("E2E-INVALID-001").isPresent());
        assertFalse(mtsMessageJpaRepository.findByMessageId("E2E-INVALID-001").isPresent());
    }
    
    @Test
    @DisplayName("다양한 메시지 타입 E2E 테스트")
    void differentMessageTypes_endToEndSuccess() {
        // Given - SMS, LMS, MMS 타입
        SmsMessage smsMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("E2E-TYPE-SMS")
            .sender("LGU_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("SMS 타입 테스트")
            .build();
            
        SmsMessage lmsMessage = SmsMessage.builder(MessageType.LMS)
            .messageId("E2E-TYPE-LMS")
            .sender("LGU_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("LMS 타입 테스트입니다. 긴 메시지 내용을 포함합니다.")
            .subject("LMS 제목")
            .build();
            
        SmsMessage mmsMessage = SmsMessage.builder(MessageType.MMS)
            .messageId("E2E-TYPE-MMS")
            .sender("MTS_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("MMS 타입 테스트")
            .subject("MMS 제목")
            .build();
        
        // When
        MessageResult smsResult = smsApplicationService.sendSms(smsMessage);
        MessageResult lmsResult = smsApplicationService.sendSms(lmsMessage);
        MessageResult mmsResult = smsApplicationService.sendSms(mmsMessage);
        
        // Then
        assertTrue(smsResult.isSuccess());
        assertTrue(lmsResult.isSuccess());
        assertTrue(mmsResult.isSuccess());
        
        // 타입별 테이블 저장 확인
        var smsEntity = lguV1MessageJpaRepository.findByMessageId("E2E-TYPE-SMS");
        assertTrue(smsEntity.isPresent());
        assertEquals(MessageType.SMS, smsEntity.get().getMessageType());
        
        var lmsEntity = lguV1MessageJpaRepository.findByMessageId("E2E-TYPE-LMS");
        assertTrue(lmsEntity.isPresent());
        assertEquals(MessageType.LMS, lmsEntity.get().getMessageType());
        
        var mmsEntity = mtsMessageJpaRepository.findByMessageId("E2E-TYPE-MMS");
        assertTrue(mmsEntity.isPresent());
        assertEquals(MessageType.MMS, mmsEntity.get().getMessageType());
        assertEquals("03", mmsEntity.get().getMessageCode()); // MMS 코드
    }
}