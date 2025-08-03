package com.bwc.messaging.sms.infrastructure.persistence.lgu.v1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 * LGU V1 전용 SMS Repository 구현체
 */
import org.springframework.context.annotation.Primary;

@Primary
@Repository("lguV1SmsRepository")
@RequiredArgsConstructor
@Slf4j
public class LguV1SmsRepositoryImpl implements SmsRepository {
    
    private final LguV1MessageJpaRepository lguV1MessageJpaRepository;
    
    @Override
    @Transactional
    public void save(SmsMessage message) {
        LguV1MessageEntity entity = convertToEntity(message);
        lguV1MessageJpaRepository.save(entity);
        log.debug("LGU V1 SMS message saved: {}", message.getMessageId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SmsMessage> findById(String messageId) {
        return lguV1MessageJpaRepository.findByMessageId(messageId)
            .map(this::convertToDomain);
    }
    
    @Override
    @Transactional
    public void updateStatus(String messageId, MessageStatus status) {
        lguV1MessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setStatus(status);
                entity.setUpdatedAt(LocalDateTime.now());
                
                if (status == MessageStatus.SENT) {
                    entity.setSentAt(LocalDateTime.now());
                } else if (status == MessageStatus.DELIVERED) {
                    entity.setDeliveredAt(LocalDateTime.now());
                }
                
                lguV1MessageJpaRepository.save(entity);
                log.debug("LGU V1 SMS status updated: {} -> {}", messageId, status);
            });
    }
    
    @Override
    @Transactional
    public void incrementRetryCount(String messageId) {
        lguV1MessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setRetryCount(entity.getRetryCount() + 1);
                entity.setUpdatedAt(LocalDateTime.now());
                lguV1MessageJpaRepository.save(entity);
                log.debug("LGU V1 SMS retry count incremented: {} ({})", 
                    messageId, entity.getRetryCount());
            });
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SmsMessage> findPendingMessages(int limit) {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        
        return lguV1MessageJpaRepository.findPendingMessages(
                MessageStatus.PENDING, currentDate, currentTime)
            .stream()
            .limit(limit)
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void healthCheck() {
        lguV1MessageJpaRepository.count();
    }
    
    /**
     * LGU V1 메시지 ID로 조회 (LGU V1 전용)
     */
    public Optional<SmsMessage> findByLguV1MessageId(String lguV1MessageId) {
        return lguV1MessageJpaRepository.findByLguV1MessageId(lguV1MessageId)
            .map(this::convertToDomain);
    }
    
    /**
     * LGU V1 응답 정보 업데이트 (LGU V1 전용)
     */
    @Transactional
    public void updateLguV1Response(String messageId, String lguV1MessageId, 
                                   String resultCode, String resultMessage, String messageKey) {
        lguV1MessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setLguV1MessageId(lguV1MessageId);
                entity.setLguResultCode(resultCode);
                entity.setLguResultMessage(resultMessage);
                entity.setLguMessageKey(messageKey);
                entity.setUpdatedAt(LocalDateTime.now());
                lguV1MessageJpaRepository.save(entity);
            });
    }
    
    /**
     * 도메인 객체를 LGU V1 Entity로 변환
     */
    private LguV1MessageEntity convertToEntity(SmsMessage message) {
        LocalDateTime now = LocalDateTime.now();
        
        return LguV1MessageEntity.builder()
            .messageId(message.getMessageId())
            .messageType(message.getType())
            .sender(message.getSender())
            .senderNumber(message.getSenderNumber())
            .receiver(message.getReceiver())
            .content(message.getContent())
            .subject(message.getSubject())
            .status(message.getStatus())
            .callbackNumber(message.getSenderNumber())
            .serviceType(message.getType().name())
            .templateId(message.getTemplateCode())
            .retryCount(0)
            .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : now)
            .sentAt(message.getSentAt())
            .updatedAt(now)
            .build();
    }
    
    /**
     * LGU V1 Entity를 도메인 객체로 변환
     */
    private SmsMessage convertToDomain(LguV1MessageEntity entity) {
        SmsMessage message = SmsMessage.builder(entity.getMessageType())
            .messageId(entity.getMessageId())
            .sender(entity.getSender())
            .senderNumber(entity.getSenderNumber())
            .receiver(entity.getReceiver())
            .content(entity.getContent())
            .subject(entity.getSubject())
            .templateCode(entity.getTemplateId())
            .build();
            
        // 상태 정보 설정
        message.setStatus(entity.getStatus());
        
        return message;
    }
}