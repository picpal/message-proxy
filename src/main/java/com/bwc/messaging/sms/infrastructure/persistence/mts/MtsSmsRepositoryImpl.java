package com.bwc.messaging.sms.infrastructure.persistence.mts;

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
 * MTS 전용 SMS Repository 구현체
 */
@Repository("mtsSmsRepository")
@RequiredArgsConstructor
@Slf4j
public class MtsSmsRepositoryImpl implements SmsRepository {
    
    private final MtsMessageJpaRepository mtsMessageJpaRepository;
    
    @Override
    @Transactional
    public void save(SmsMessage message) {
        MtsMessageEntity entity = convertToEntity(message);
        mtsMessageJpaRepository.save(entity);
        log.debug("MTS SMS message saved: {}", message.getMessageId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SmsMessage> findById(String messageId) {
        return mtsMessageJpaRepository.findByMessageId(messageId)
            .map(this::convertToDomain);
    }
    
    @Override
    @Transactional
    public void updateStatus(String messageId, MessageStatus status) {
        mtsMessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setStatus(status);
                entity.setUpdatedAt(LocalDateTime.now());
                
                if (status == MessageStatus.SENT) {
                    entity.setSentAt(LocalDateTime.now());
                    entity.setSendDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                } else if (status == MessageStatus.DELIVERED) {
                    entity.setDeliveredAt(LocalDateTime.now());
                    entity.setRsltDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                }
                
                mtsMessageJpaRepository.save(entity);
                log.debug("MTS SMS status updated: {} -> {}", messageId, status);
            });
    }
    
    @Override
    @Transactional
    public void incrementRetryCount(String messageId) {
        mtsMessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setRetryCount(entity.getRetryCount() + 1);
                entity.setUpdatedAt(LocalDateTime.now());
                mtsMessageJpaRepository.save(entity);
                log.debug("MTS SMS retry count incremented: {} ({})", 
                    messageId, entity.getRetryCount());
            });
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SmsMessage> findPendingMessages(int limit) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        return mtsMessageJpaRepository.findPendingMessages(MessageStatus.PENDING, currentDateTime)
            .stream()
            .limit(limit)
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void healthCheck() {
        mtsMessageJpaRepository.count();
    }
    
    /**
     * 도메인 객체를 MTS Entity로 변환
     */
    private MtsMessageEntity convertToEntity(SmsMessage message) {
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        return MtsMessageEntity.builder()
            .messageId(message.getMessageId())
            .messageType(message.getType())
            .sender(message.getSender())
            .senderNumber(message.getSenderNumber())
            .receiver(message.getReceiver())
            .content(message.getContent())
            .subject(message.getSubject())
            .status(message.getStatus())
            .messageCode(determineMessageCode(message))
            .telecomCode(determineTelecomCode(message.getReceiver()))
            .destPhone(formatPhoneForMts(message.getReceiver()))
            .sendPhone(formatPhoneForMts(message.getSenderNumber()))
            .msgBody(message.getContent())
            .companyId("DEFAULT_COMPANY")
            .serviceCd("SMS")
            .retryCount(0)
            .regDt(nowString)
            .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : now)
            .sentAt(message.getSentAt())
            .updatedAt(now)
            .build();
    }
    
    /**
     * MTS Entity를 도메인 객체로 변환
     */
    private SmsMessage convertToDomain(MtsMessageEntity entity) {
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
    
    private String determineMessageCode(SmsMessage message) {
        return switch (message.getType()) {
            case SMS -> "01";
            case LMS -> "02";
            case MMS -> "03";
            default -> "01";
        };
    }
    
    private String determineTelecomCode(String phoneNumber) {
        if (phoneNumber == null) return "01";
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        if (cleanNumber.startsWith("010") || cleanNumber.startsWith("011")) {
            return "01"; // SKT
        } else if (cleanNumber.startsWith("016") || cleanNumber.startsWith("017")) {
            return "02"; // KT
        } else if (cleanNumber.startsWith("018") || cleanNumber.startsWith("019")) {
            return "03"; // LGU+
        }
        
        return "01"; // 기본값
    }
    
    private String formatPhoneForMts(String phoneNumber) {
        if (phoneNumber == null) return "";
        return phoneNumber.replaceAll("[^0-9]", "");
    }
}