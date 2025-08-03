package com.bwc.messaging.sms.infrastructure.persistence;

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

/**
 * SMS Repository JPA 구현체 - SMS 전용 Entity 사용
 */
@Repository
@RequiredArgsConstructor
public class SmsRepositoryImpl implements SmsRepository {
    
    private final SmsMessageJpaRepository smsMessageJpaRepository;
    
    @Override
    @Transactional
    public void save(SmsMessage message) {
        SmsMessageEntity entity = convertToEntity(message);
        smsMessageJpaRepository.save(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SmsMessage> findById(String messageId) {
        return smsMessageJpaRepository.findByMessageId(messageId)
            .map(this::convertToDomain);
    }
    
    @Override
    @Transactional
    public void updateStatus(String messageId, MessageStatus status) {
        smsMessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setStatus(status);
                entity.setUpdatedAt(LocalDateTime.now());
                if (status == MessageStatus.SENT) {
                    entity.setSentAt(LocalDateTime.now());
                }
                smsMessageJpaRepository.save(entity);
            });
    }
    
    @Override
    @Transactional
    public void incrementRetryCount(String messageId) {
        smsMessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setRetryCount(entity.getRetryCount() + 1);
                entity.setUpdatedAt(LocalDateTime.now());
                smsMessageJpaRepository.save(entity);
            });
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SmsMessage> findPendingMessages(int limit) {
        return smsMessageJpaRepository.findByStatusOrderByCreatedAtAsc(MessageStatus.PENDING)
            .stream()
            .limit(limit)
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }
    
    /**
     * 헬스 체크용 메소드
     */
    public void healthCheck() {
        smsMessageJpaRepository.count(); // 단순 쿼리로 DB 연결 확인
    }
    
    /**
     * 도메인 객체를 Entity로 변환
     */
    private SmsMessageEntity convertToEntity(SmsMessage message) {
        LocalDateTime now = LocalDateTime.now();
        
        return SmsMessageEntity.builder()
            .messageId(message.getMessageId())
            .type(message.getType())
            .sender(message.getSender())
            .senderNumber(message.getSenderNumber())
            .receiver(message.getReceiver())
            .content(message.getContent())
            .subject(message.getSubject())
            .templateCode(message.getTemplateCode())
            .status(message.getStatus())
            .channel(message.getChannel())
            .retryCount(0)
            .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : now)
            .sentAt(message.getSentAt())
            .updatedAt(now)
            .build();
    }
    
    /**
     * Entity를 도메인 객체로 변환
     */
    private SmsMessage convertToDomain(SmsMessageEntity entity) {
        SmsMessage message = SmsMessage.builder(entity.getType())
            .messageId(entity.getMessageId())
            .sender(entity.getSender())
            .senderNumber(entity.getSenderNumber())
            .receiver(entity.getReceiver())
            .content(entity.getContent())
            .subject(entity.getSubject())
            .templateCode(entity.getTemplateCode())
            .build();
            
        // 상태 정보 설정
        message.setStatus(entity.getStatus());
        message.setChannel(entity.getChannel());
        
        return message;
    }
}