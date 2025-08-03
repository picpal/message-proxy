package com.bwc.messaging.sms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SMS 리포지토리 통합 테스트")
class SmsRepositoryImplIntegrationTest {

    @Autowired
    private SmsRepository smsRepository;
    
    private SmsMessage testMessage;

    @BeforeEach
    void setUp() {
        testMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("integration-test-001")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("통합 테스트 메시지")
            .build();
        testMessage.setStatus(MessageStatus.PENDING);
        testMessage.setChannel(MessageChannel.LGU_V1);
    }

    @Test
    @DisplayName("SMS 메시지 저장 및 조회 통합 테스트")
    void saveAndFindMessageIntegration() {
        // when
        smsRepository.save(testMessage);

        // then
        Optional<SmsMessage> found = smsRepository.findById(testMessage.getMessageId());
        assertThat(found).isPresent();
        
        SmsMessage foundMessage = found.get();
        assertThat(foundMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(foundMessage.getSender()).isEqualTo(testMessage.getSender());
        assertThat(foundMessage.getSenderNumber()).isEqualTo(testMessage.getSenderNumber());
        assertThat(foundMessage.getReceiver()).isEqualTo(testMessage.getReceiver());
        assertThat(foundMessage.getContent()).isEqualTo(testMessage.getContent());
        assertThat(foundMessage.getType()).isEqualTo(MessageType.SMS);
        assertThat(foundMessage.getStatus()).isEqualTo(MessageStatus.PENDING);
    }

    @Test
    @DisplayName("상태 업데이트 통합 테스트")
    void updateStatusIntegration() {
        // given
        smsRepository.save(testMessage);

        // when
        smsRepository.updateStatus(testMessage.getMessageId(), MessageStatus.SENT);

        // then
        Optional<SmsMessage> found = smsRepository.findById(testMessage.getMessageId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(MessageStatus.SENT);
    }

    @Test
    @DisplayName("PENDING 메시지 조회 통합 테스트")
    void findPendingMessagesIntegration() {
        // given
        SmsMessage message1 = createTestMessage("pending-int-001", MessageStatus.PENDING);
        SmsMessage message2 = createTestMessage("pending-int-002", MessageStatus.PENDING);
        SmsMessage message3 = createTestMessage("sent-int-001", MessageStatus.SENT);
        
        smsRepository.save(message1);
        smsRepository.save(message2);
        smsRepository.save(message3);

        // when
        List<SmsMessage> pendingMessages = smsRepository.findPendingMessages(10);

        // then
        assertThat(pendingMessages).hasSizeGreaterThanOrEqualTo(2);
        assertThat(pendingMessages)
            .allMatch(msg -> msg.getStatus() == MessageStatus.PENDING);
    }

    private SmsMessage createTestMessage(String messageId, MessageStatus status) {
        SmsMessage message = SmsMessage.builder(MessageType.SMS)
            .messageId(messageId)
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("통합 테스트 메시지 " + messageId)
            .build();
        message.setStatus(status);
        return message;
    }
}