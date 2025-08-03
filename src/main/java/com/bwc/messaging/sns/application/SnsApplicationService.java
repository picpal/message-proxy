package com.bwc.messaging.sns.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sns.application.strategy.SnsStrategyFactory;
import com.bwc.messaging.sns.domain.SnsMessage;
import com.bwc.messaging.sns.domain.SnsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsApplicationService {
    
    private final SnsRepository snsRepository;
    private final SnsStrategyFactory strategyFactory;
    private final SnsChannelRouter channelRouter;
    
    @Transactional
    public MessageResult sendSns(SnsMessage message) {
        log.info("Sending SNS message: {}", message.getMessageId());
        
        try {
            // 1. 메시지 유효성 검증
            if (!message.isValid()) {
                return MessageResult.failure(message.getMessageId(), "INVALID_MESSAGE", "Invalid message format");
            }
            
            // 2. 메시지 저장
            snsRepository.save(message);
            
            // 3. 발송 채널 결정
            var channel = channelRouter.selectChannel(message);
            
            // 4. 전략 패턴으로 발송
            var strategy = strategyFactory.getStrategy(channel);
            MessageResult result = strategy.send(message);
            
            // 5. 상태 업데이트
            snsRepository.updateStatus(message.getMessageId(), result.getStatus());
            
            log.info("SNS sent successfully: {} via {}", message.getMessageId(), channel);
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send SNS: {}", message.getMessageId(), e);
            snsRepository.updateStatus(message.getMessageId(), MessageStatus.FAILED);
            return MessageResult.failure(message.getMessageId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public MessageStatus getSnsStatus(String messageId) {
        return snsRepository.findById(messageId)
            .map(SnsMessage::getStatus)
            .orElse(MessageStatus.FAILED);
    }
    
    @Transactional
    public void retrySns(String messageId) {
        snsRepository.findById(messageId)
            .ifPresent(message -> {
                snsRepository.incrementRetryCount(messageId);
                sendSns(message);
            });
    }
}