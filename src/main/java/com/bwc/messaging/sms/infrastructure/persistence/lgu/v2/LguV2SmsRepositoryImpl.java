package com.bwc.messaging.sms.infrastructure.persistence.lgu.v2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LGU V2 전용 SMS Repository 구현체
 */
@Repository("lguV2SmsRepository")
@RequiredArgsConstructor
@Slf4j
public class LguV2SmsRepositoryImpl implements SmsRepository {
    
    private final LguV2MessageJpaRepository lguV2MessageJpaRepository;
    
    @Override
    @Transactional
    public void save(SmsMessage message) {
        LguV2MessageEntity entity = convertToEntity(message);
        lguV2MessageJpaRepository.save(entity);
        log.debug("LGU V2 SMS message saved: {}", message.getMessageId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SmsMessage> findById(String messageId) {
        return lguV2MessageJpaRepository.findByMessageId(messageId)
            .map(this::convertToDomain);
    }
    
    @Override
    @Transactional
    public void updateStatus(String messageId, MessageStatus status) {
        lguV2MessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setStatus(status);
                entity.setUpdatedAt(LocalDateTime.now());
                
                if (status == MessageStatus.SENT) {
                    entity.setSentAt(LocalDateTime.now());
                } else if (status == MessageStatus.DELIVERED) {
                    entity.setDeliveredAt(LocalDateTime.now());
                }
                
                lguV2MessageJpaRepository.save(entity);
                log.debug("LGU V2 SMS status updated: {} -> {}", messageId, status);
            });
    }
    
    @Override
    @Transactional
    public void incrementRetryCount(String messageId) {
        lguV2MessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setRetryCount(entity.getRetryCount() + 1);
                entity.setUpdatedAt(LocalDateTime.now());
                lguV2MessageJpaRepository.save(entity);
                log.debug("LGU V2 SMS retry count incremented: {} ({})", 
                    messageId, entity.getRetryCount());
            });
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SmsMessage> findPendingMessages(int limit) {
        return lguV2MessageJpaRepository.findPendingMessages(MessageStatus.PENDING, LocalDateTime.now())
            .stream()
            .limit(limit)
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void healthCheck() {
        lguV2MessageJpaRepository.count();
    }
    
    /**
     * 도메인 객체를 LGU V2 Entity로 변환
     */
    private LguV2MessageEntity convertToEntity(SmsMessage message) {
        LocalDateTime now = LocalDateTime.now();
        
        return LguV2MessageEntity.builder()
            .messageId(message.getMessageId())
            .messageType(message.getType())
            .sender(message.getSender())
            .senderNumber(message.getSenderNumber())
            .receiver(message.getReceiver())
            .content(message.getContent())
            .subject(message.getSubject())
            .status(message.getStatus())
            .contentType(message.getType().name())
            .retryCount(0)
            .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : now)
            .sentAt(message.getSentAt())
            .updatedAt(now)
            .build();
    }
    
    /**
     * LGU V2 Entity를 도메인 객체로 변환
     */
    private SmsMessage convertToDomain(LguV2MessageEntity entity) {
        SmsMessage message = SmsMessage.builder(entity.getMessageType())
            .messageId(entity.getMessageId())
            .sender(entity.getSender())
            .senderNumber(entity.getSenderNumber())
            .receiver(entity.getReceiver())
            .content(entity.getContent())
            .subject(entity.getSubject())
            .build();
            
        message.setStatus(entity.getStatus());
        return message;
    }
}