package com.bwc.messaging.sns.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sns.domain.SnsButton;
import com.bwc.messaging.sns.domain.SnsField;
import com.bwc.messaging.sns.domain.SnsMessage;
import com.bwc.messaging.sns.domain.SnsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SNS Repository JPA 구현체
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class SnsRepositoryImpl implements SnsRepository {
    
    private final SnsMessageJpaRepository snsMessageJpaRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public void save(SnsMessage message) {
        SnsMessageEntity entity = convertToEntity(message);
        snsMessageJpaRepository.save(entity);
        log.debug("SNS message saved: {}", message.getMessageId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SnsMessage> findById(String messageId) {
        return snsMessageJpaRepository.findByMessageId(messageId)
            .map(this::convertToDomain);
    }
    
    @Override
    @Transactional
    public void updateStatus(String messageId, MessageStatus status) {
        snsMessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setStatus(status);
                entity.setUpdatedAt(LocalDateTime.now());
                
                if (status == MessageStatus.SENT) {
                    entity.setSentAt(LocalDateTime.now());
                }
                
                snsMessageJpaRepository.save(entity);
                log.debug("SNS message status updated: {} -> {}", messageId, status);
            });
    }
    
    @Override
    @Transactional
    public void incrementRetryCount(String messageId) {
        snsMessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setRetryCount(entity.getRetryCount() + 1);
                entity.setUpdatedAt(LocalDateTime.now());
                snsMessageJpaRepository.save(entity);
                log.debug("SNS message retry count incremented: {} ({})", 
                    messageId, entity.getRetryCount());
            });
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SnsMessage> findPendingMessages(int limit) {
        return snsMessageJpaRepository.findRetryTargetMessages(MessageStatus.PENDING)
            .stream()
            .limit(limit)
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<SnsMessage> findByChannelId(String channelId, int limit) {
        // TODO: channelId 기반 조회 구현 필요
        return findPendingMessages(limit);
    }
    
    @Override
    public void healthCheck() {
        snsMessageJpaRepository.count();
    }
    
    /**
     * 발송사 메시지 ID로 조회
     */
    public Optional<SnsMessage> findByVendorMessageId(String vendorMessageId) {
        return snsMessageJpaRepository.findByVendorMessageId(vendorMessageId)
            .map(this::convertToDomain);
    }
    
    /**
     * 발송사 응답 정보 업데이트
     */
    @Transactional
    public void updateVendorResponse(String messageId, String vendorMessageId, 
                                   String errorCode, String errorMessage) {
        snsMessageJpaRepository.findByMessageId(messageId)
            .ifPresent(entity -> {
                entity.setVendorMessageId(vendorMessageId);
                entity.setErrorCode(errorCode);
                entity.setErrorMessage(errorMessage);
                entity.setUpdatedAt(LocalDateTime.now());
                snsMessageJpaRepository.save(entity);
            });
    }
    
    /**
     * 도메인 객체를 Entity로 변환
     */
    private SnsMessageEntity convertToEntity(SnsMessage message) {
        LocalDateTime now = LocalDateTime.now();
        
        return SnsMessageEntity.builder()
            .messageId(message.getMessageId())
            .type(message.getType())
            .sender(message.getSender())
            .content(message.getContent())
            .status(message.getStatus())
            .channel(message.getChannel())
            .webhookUrl(message.getWebhookUrl())
            .isEmbed(message.isEmbed())
            .title(message.getTitle())
            .description(message.getDescription())
            .color(message.getColor())
            .thumbnailUrl(message.getThumbnailUrl())
            .imageUrl(message.getImageUrl())
            .fieldsJson(serializeFields(message.getFields()))
            .buttonsJson(serializeButtons(message.getButtons()))
            .retryCount(0)
            .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : now)
            .sentAt(message.getSentAt())
            .updatedAt(now)
            .build();
    }
    
    /**
     * Entity를 도메인 객체로 변환
     */
    private SnsMessage convertToDomain(SnsMessageEntity entity) {
        SnsMessage message = SnsMessage.builder(entity.getType())
            .messageId(entity.getMessageId())
            .sender(entity.getSender())
            .content(entity.getContent())
            .webhookUrl(entity.getWebhookUrl())
            .asEmbed(entity.getIsEmbed())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .color(entity.getColor())
            .thumbnailUrl(entity.getThumbnailUrl())
            .imageUrl(entity.getImageUrl())
            .fields(deserializeFields(entity.getFieldsJson()))
            .buttons(deserializeButtons(entity.getButtonsJson()))
            .build();
            
        message.setStatus(entity.getStatus());
        message.setChannel(entity.getChannel());
        
        return message;
    }
    
    private String serializeFields(List<SnsField> fields) {
        if (fields == null || fields.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(fields);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize fields", e);
            return null;
        }
    }
    
    private List<SnsField> deserializeFields(String fieldsJson) {
        if (fieldsJson == null || fieldsJson.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(fieldsJson, new TypeReference<List<SnsField>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize fields: {}", fieldsJson, e);
            return List.of();
        }
    }
    
    private String serializeButtons(List<SnsButton> buttons) {
        if (buttons == null || buttons.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(buttons);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize buttons", e);
            return null;
        }
    }
    
    private List<SnsButton> deserializeButtons(String buttonsJson) {
        if (buttonsJson == null || buttonsJson.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(buttonsJson, new TypeReference<List<SnsButton>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize buttons: {}", buttonsJson, e);
            return List.of();
        }
    }
}