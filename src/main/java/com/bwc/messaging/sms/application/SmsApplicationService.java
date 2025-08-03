package com.bwc.messaging.sms.application;

import com.bwc.messaging.shared.domain.MessageType;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sms.application.strategy.SmsStrategyFactory;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsApplicationService {
    
    private final SmsRepository smsRepository;
    private final SmsStrategyFactory strategyFactory;
    private final SmsChannelRouter channelRouter;
    
    @Transactional
    public MessageResult sendSms(SmsMessage message) {
        log.info("Sending SMS message: {}", message.getMessageId());
        
        try {
            // 1. 메시지 타입 결정 (SMS/LMS)
            if (message.getContent().getBytes().length > 80 && message.getType() == MessageType.SMS) {
                message.setType(MessageType.LMS);
            }

            // 2. 메시지 유효성 검증
            if (!message.isValid()) {
                return MessageResult.failure(message.getMessageId(), "INVALID_MESSAGE", "Invalid message format");
            }
            
            // 1. 메시지 유효성 검증
            if (!message.isValid()) {
                return MessageResult.failure(message.getMessageId(), "INVALID_MESSAGE", "Invalid message format");
            }

            // 2. 메시지 타입 결정 (SMS/LMS)
            if (message.getContent().getBytes().length > 80 && message.getType() == MessageType.SMS) {
                message.setType(MessageType.LMS);
            }

            // 3. 메시지 저장
            smsRepository.save(message);
            
            // 4. 발송 채널 결정
            MessageChannel channel = channelRouter.selectChannel(message);
            
            // 5. 전략 패턴으로 발송
            var strategy = strategyFactory.getStrategy(channel);
            MessageResult result = strategy.send(message);
            
            // 6. 상태 업데이트
            smsRepository.updateStatus(message.getMessageId(), result.getStatus());
            
            log.info("SMS sent successfully: {} via {}", message.getMessageId(), channel);
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", message.getMessageId(), e);
            smsRepository.updateStatus(message.getMessageId(), MessageStatus.FAILED);
            return MessageResult.failure(message.getMessageId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public MessageStatus getSmsStatus(String messageId) {
        return smsRepository.findById(messageId)
            .map(SmsMessage::getStatus)
            .orElse(MessageStatus.FAILED);
    }
    
    @Transactional
    public void retrySms(String messageId) {
        smsRepository.findById(messageId)
            .ifPresent(message -> {
                smsRepository.incrementRetryCount(messageId);
                sendSms(message);
            });
    }
}