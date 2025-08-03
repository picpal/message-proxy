package com.bwc.messaging.sms.application.strategy.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sms.application.strategy.SmsStrategy;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MtsSmsStrategy implements SmsStrategy {
    
    private final SmsRepository smsRepository;
    
    @Override
    public MessageResult send(SmsMessage message) {
        log.info("Storing SMS for MTS agent pickup: {}", message.getMessageId());
        
        try {
            message.setChannel(MessageChannel.MTS);
            message.setStatus(MessageStatus.PENDING);
            smsRepository.save(message);
            
            log.info("SMS stored successfully for MTS agent pickup: {}", message.getMessageId());
            return MessageResult.success(message.getMessageId(), message.getMessageId());
            
        } catch (Exception e) {
            log.error("Failed to store SMS for MTS: {}", message.getMessageId(), e);
            return MessageResult.failure(message.getMessageId(), "DB_ERROR", e.getMessage());
        }
    }
    
    @Override
    public MessageStatus getStatus(String messageId) {
        return smsRepository.findById(messageId)
            .map(SmsMessage::getStatus)
            .orElse(MessageStatus.FAILED);
    }
    
    @Override
    public boolean canSend(SmsMessage message) {
        return message.isValid() && 
               isHealthy() && 
               getSupportedChannel().supports(message.getType());
    }
    
    @Override
    public Class<SmsMessage> getSupportedMessageType() {
        return SmsMessage.class;
    }
    
    @Override
    public MessageChannel getSupportedChannel() {
        return MessageChannel.MTS;
    }
    
    @Override
    public boolean isHealthy() {
        try {
            smsRepository.healthCheck();
            return true;
        } catch (Exception e) {
            log.warn("MTS SMS strategy health check failed", e);
            return false;
        }
    }
}