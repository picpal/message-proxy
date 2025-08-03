package com.bwc.messaging.sms.application.strategy.impl;

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
public class LguV1SmsStrategy implements SmsStrategy {
    
    private final SmsRepository smsRepository;
    
    @Override
    public MessageResult send(SmsMessage message) {
        log.info("Storing SMS for LGU V1 agent pickup: {}", message.getMessageId());
        
        try {
            // DB에 저장 - Agent가 읽어서 처리
            message.setChannel(MessageChannel.LGU_V1);
            message.setStatus(MessageStatus.PENDING);
            smsRepository.save(message);
            
            log.info("SMS stored successfully for agent pickup: {}", message.getMessageId());
            return MessageResult.success(message.getMessageId(), message.getMessageId());
            
        } catch (Exception e) {
            log.error("Failed to store SMS for LGU V1: {}", message.getMessageId(), e);
            return MessageResult.failure(message.getMessageId(), "DB_ERROR", e.getMessage());
        }
    }
    
    @Override
    public MessageStatus getStatus(String messageId) {
        // DB에서 상태 조회 - Agent가 상태 업데이트
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
        return MessageChannel.LGU_V1;
    }
    
    @Override
    public boolean isHealthy() {
        try {
            // DB 연결 상태 확인
            smsRepository.healthCheck();
            return true;
        } catch (Exception e) {
            log.warn("LGU V1 SMS strategy health check failed", e);
            return false;
        }
    }
}