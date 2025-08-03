package com.bwc.messaging.sms.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.domain.SmsMessage;

@DisplayName("SmsChannelRouter 테스트")
class SmsChannelRouterTest {

    private SmsChannelRouter smsChannelRouter;
    
    @BeforeEach
    void setUp() {
        smsChannelRouter = new SmsChannelRouter();
    }
    
    @Test
    @DisplayName("LGU 서비스 코드로 채널 선택")
    void selectChannel_LguServiceCode() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-001")
            .sender("LGU_TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("단문 메시지")
            .build();
        
        // When
        MessageChannel channel = smsChannelRouter.selectChannel(message);
        
        // Then
        assertEquals(MessageChannel.LGU_V1, channel);
    }
    
    @Test
    @DisplayName("LGU 긴 메시지로 V2 채널 선택")
    void selectChannel_LguLongMessage() {
        // Given
        String longContent = "이것은 매우 긴 메시지입니다. ".repeat(50); // 1000자 이상
        SmsMessage message = SmsMessage.builder(MessageType.LMS)
            .messageId("MSG-002")
            .sender("LGU_TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent)
            .build();
        
        // When
        MessageChannel channel = smsChannelRouter.selectChannel(message);
        
        // Then
        assertEquals(MessageChannel.LGU_V2, channel);
    }
    
    @Test
    @DisplayName("MTS 서비스 코드로 채널 선택")
    void selectChannel_MtsServiceCode() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-003")
            .sender("MTS_TEST")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("MTS 메시지")
            .build();
        
        // When
        MessageChannel channel = smsChannelRouter.selectChannel(message);
        
        // Then
        assertEquals(MessageChannel.MTS, channel);
    }
    
    @Test
    @DisplayName("기본 채널 선택 (서비스 코드 없음)")
    void selectChannel_DefaultChannel() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-004")
            .sender("OTHER_SERVICE")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("일반 메시지")
            .build();
        
        // When
        MessageChannel channel = smsChannelRouter.selectChannel(message);
        
        // Then
        assertEquals(MessageChannel.LGU_V1, channel);
    }
    
    @Test
    @DisplayName("null 서비스 코드로 기본 채널 선택")
    void selectChannel_NullServiceCode() {
        // Given
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId("MSG-005")
            .sender(null)
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("null sender 메시지")
            .build();
        
        // When
        MessageChannel channel = smsChannelRouter.selectChannel(message);
        
        // Then
        assertEquals(MessageChannel.LGU_V1, channel);
    }
    
    @Test
    @DisplayName("LGU V1 실패 시 V2 대체 채널")
    void selectFallbackChannel_LguV1ToV2() {
        // When
        MessageChannel fallbackChannel = smsChannelRouter.selectFallbackChannel(MessageChannel.LGU_V1);
        
        // Then
        assertEquals(MessageChannel.LGU_V2, fallbackChannel);
    }
    
    @Test
    @DisplayName("LGU V2 실패 시 MTS 대체 채널")
    void selectFallbackChannel_LguV2ToMts() {
        // When
        MessageChannel fallbackChannel = smsChannelRouter.selectFallbackChannel(MessageChannel.LGU_V2);
        
        // Then
        assertEquals(MessageChannel.MTS, fallbackChannel);
    }
    
    @Test
    @DisplayName("MTS 실패 시 LGU V1 대체 채널")
    void selectFallbackChannel_MtsToLguV1() {
        // When
        MessageChannel fallbackChannel = smsChannelRouter.selectFallbackChannel(MessageChannel.MTS);
        
        // Then
        assertEquals(MessageChannel.LGU_V1, fallbackChannel);
    }
    
    @Test
    @DisplayName("기타 채널 실패 시 기본 대체 채널")
    void selectFallbackChannel_DefaultFallback() {
        // When
        MessageChannel fallbackChannel = smsChannelRouter.selectFallbackChannel(MessageChannel.SMTP);
        
        // Then
        assertEquals(MessageChannel.LGU_V1, fallbackChannel);
    }
}