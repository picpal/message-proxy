package com.bwc.messaging.sms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.domain.SmsMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("SMS 리포지토리 단위 테스트")
class SmsRepositoryImplTest {

    @Mock
    private SmsMessageJpaRepository smsMessageJpaRepository;
    
    private SmsRepositoryImpl smsRepository;
    private SmsMessage testMessage;

    @BeforeEach
    void setUp() {
        smsRepository = new SmsRepositoryImpl(smsMessageJpaRepository);
        
        testMessage = SmsMessage.builder(MessageType.SMS)
            .messageId("test-repo-001")
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .build();
        testMessage.setStatus(MessageStatus.PENDING);
        testMessage.setChannel(MessageChannel.LGU_V1);
    }

    @Test
    @DisplayName("SMS 메시지를 저장한다")
    void saveMessage() {
        // when
        smsRepository.save(testMessage);

        // then
        verify(smsMessageJpaRepository).save(any(SmsMessageEntity.class));
    }

    @Test
    @DisplayName("SMS 메시지를 조회한다")
    void findMessage() {
        // given
        SmsMessageEntity entity = createTestEntity();
        when(smsMessageJpaRepository.findByMessageId(testMessage.getMessageId()))
            .thenReturn(Optional.of(entity));

        // when
        Optional<SmsMessage> found = smsRepository.findById(testMessage.getMessageId());

        // then
        assertThat(found).isPresent();
        SmsMessage foundMessage = found.get();
        assertThat(foundMessage.getMessageId()).isEqualTo(testMessage.getMessageId());
        assertThat(foundMessage.getSender()).isEqualTo(testMessage.getSender());
        assertThat(foundMessage.getType()).isEqualTo(MessageType.SMS);
        
        verify(smsMessageJpaRepository).findByMessageId(testMessage.getMessageId());
    }

    @Test
    @DisplayName("메시지 상태를 업데이트한다")
    void updateMessageStatus() {
        // given
        SmsMessageEntity entity = createTestEntity();
        when(smsMessageJpaRepository.findByMessageId(testMessage.getMessageId()))
            .thenReturn(Optional.of(entity));

        // when
        smsRepository.updateStatus(testMessage.getMessageId(), MessageStatus.SENT);

        // then
        verify(smsMessageJpaRepository).findByMessageId(testMessage.getMessageId());
        verify(smsMessageJpaRepository).save(entity);
        assertThat(entity.getStatus()).isEqualTo(MessageStatus.SENT);
        assertThat(entity.getSentAt()).isNotNull();
    }

    @Test
    @DisplayName("재시도 횟수를 증가시킨다")
    void incrementRetryCount() {
        // given
        SmsMessageEntity entity = createTestEntity();
        entity.setRetryCount(0);
        when(smsMessageJpaRepository.findByMessageId(testMessage.getMessageId()))
            .thenReturn(Optional.of(entity));

        // when
        smsRepository.incrementRetryCount(testMessage.getMessageId());

        // then
        verify(smsMessageJpaRepository).findByMessageId(testMessage.getMessageId());
        verify(smsMessageJpaRepository).save(entity);
        assertThat(entity.getRetryCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("PENDING 상태의 메시지들을 조회한다")
    void findPendingMessages() {
        // given
        SmsMessageEntity entity1 = createTestEntity("pending-001", MessageStatus.PENDING);
        SmsMessageEntity entity2 = createTestEntity("pending-002", MessageStatus.PENDING);
        List<SmsMessageEntity> pendingEntities = List.of(entity1, entity2);
        
        when(smsMessageJpaRepository.findByStatusOrderByCreatedAtAsc(MessageStatus.PENDING))
            .thenReturn(pendingEntities);

        // when
        List<SmsMessage> pendingMessages = smsRepository.findPendingMessages(10);

        // then
        assertThat(pendingMessages).hasSize(2);
        assertThat(pendingMessages)
            .extracting(SmsMessage::getMessageId)
            .containsExactly("pending-001", "pending-002");
        
        verify(smsMessageJpaRepository).findByStatusOrderByCreatedAtAsc(MessageStatus.PENDING);
    }

    @Test
    @DisplayName("헬스 체크가 정상 동작한다")
    void healthCheck() {
        // given
        when(smsMessageJpaRepository.count()).thenReturn(5L);

        // when & then
        assertThatCode(() -> smsRepository.healthCheck()).doesNotThrowAnyException();
        verify(smsMessageJpaRepository).count();
    }

    private SmsMessageEntity createTestEntity() {
        return createTestEntity(testMessage.getMessageId(), MessageStatus.PENDING);
    }

    private SmsMessageEntity createTestEntity(String messageId, MessageStatus status) {
        return SmsMessageEntity.builder()
            .messageId(messageId)
            .type(MessageType.SMS)
            .sender("TestApp")
            .senderNumber("01012345678")
            .receiver("01087654321")
            .content("테스트 메시지")
            .status(status)
            .channel(MessageChannel.LGU_V1)
            .retryCount(0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}