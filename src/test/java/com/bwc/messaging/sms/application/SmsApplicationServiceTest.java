package com.bwc.messaging.sms.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.application.strategy.SmsStrategy;
import com.bwc.messaging.sms.application.strategy.SmsStrategyFactory;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SMS 애플리케이션 서비스 테스트")
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

    private SmsMessage testMessage;

    @BeforeEach
    void setUp() {
        testMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("test-msg-001")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();
    }

    @Test
    @DisplayName("유효한 SMS 메시지를 성공적으로 발송한다")
    void sendValidSmsMessage() {
        // given
        MessageChannel selectedChannel = MessageChannel.LGU_V1;
        MessageResult expectedResult = MessageResult.success(testMessage.getMessageId(), "MSG_001");
        
        when(channelRouter.selectChannel(testMessage)).thenReturn(selectedChannel);
        when(strategyFactory.getStrategy(selectedChannel)).thenReturn(smsStrategy);
        when(smsStrategy.send(testMessage)).thenReturn(expectedResult);

        // when
        MessageResult result = smsApplicationService.sendSms(testMessage);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessageId()).isEqualTo(testMessage.getMessageId());
        
        verify(smsRepository).save(testMessage);
        verify(smsRepository).updateStatus(testMessage.getMessageId(), MessageStatus.SENT);
        verify(channelRouter).selectChannel(testMessage);
        verify(strategyFactory).getStrategy(selectedChannel);
        verify(smsStrategy).send(testMessage);
    }

    @Test
    @DisplayName("잘못된 메시지 형식일 때 실패를 반환한다")
    void sendInvalidMessage() {
        // given - 잘못된 전화번호
        SmsMessage invalidMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("invalid-001")
            .sender("TestApp")
            .senderNumber("invalid-number")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();

        // when
        MessageResult result = smsApplicationService.sendSms(invalidMessage);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("INVALID_MESSAGE");
        
        verify(smsRepository, never()).save(any());
        verify(channelRouter, never()).selectChannel(any());
    }

    @Test
    @DisplayName("긴 메시지는 자동으로 LMS로 변환된다")
    void autoConvertToLms() {
        // given - 80바이트 초과 메시지
        String longContent = "a".repeat(85);
        SmsMessage longMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("long-msg-001")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content(longContent)
            .build();

        MessageChannel selectedChannel = MessageChannel.LGU_V2;
        MessageResult expectedResult = MessageResult.success(longMessage.getMessageId(), "MSG_002");
        
        when(channelRouter.selectChannel(any())).thenReturn(selectedChannel);
        when(strategyFactory.getStrategy(selectedChannel)).thenReturn(smsStrategy);
        when(smsStrategy.send(any())).thenReturn(expectedResult);

        // when
        MessageResult result = smsApplicationService.sendSms(longMessage);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(longMessage.getType()).isEqualTo(MessageType.LMS);
        
        verify(smsRepository).save(longMessage);
        verify(smsRepository).updateStatus(longMessage.getMessageId(), MessageStatus.SENT);
    }

    @Test
    @DisplayName("메시지 상태를 조회한다")
    void getSmsStatus() {
        // given
        String messageId = "test-status-001";
        when(smsRepository.findById(messageId)).thenReturn(java.util.Optional.of(testMessage));
        testMessage.setStatus(MessageStatus.SENT);

        // when
        MessageStatus status = smsApplicationService.getSmsStatus(messageId);

        // then
        assertThat(status).isEqualTo(MessageStatus.SENT);
        verify(smsRepository).findById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메시지 상태 조회 시 FAILED를 반환한다")
    void getNonExistentMessageStatus() {
        // given
        String messageId = "non-existent";
        when(smsRepository.findById(messageId)).thenReturn(java.util.Optional.empty());

        // when
        MessageStatus status = smsApplicationService.getSmsStatus(messageId);

        // then
        assertThat(status).isEqualTo(MessageStatus.FAILED);
        verify(smsRepository).findById(messageId);
    }
}