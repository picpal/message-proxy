package com.bwc.messaging.email.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwc.messaging.email.application.strategy.EmailStrategyFactory;
import com.bwc.messaging.email.domain.EmailMessage;
import com.bwc.messaging.email.domain.EmailRepository;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated 헥사고날 아키텍처 적용으로 EmailService로 대체 예정
 */
@Deprecated
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailApplicationService {
    
    private final EmailRepository emailRepository;
    private final EmailStrategyFactory strategyFactory;
    private final EmailChannelRouter channelRouter;
    
    @Transactional
    public MessageResult sendEmail(EmailMessage message) {
        log.info("Sending email message: {}", message.getMessageId());
        
        try {
            // 1. 메시지 유효성 검증
            if (!message.isValid()) {
                return MessageResult.failure(message.getMessageId(), "INVALID_MESSAGE", "Invalid email format");
            }
            
            // 2. 메시지 저장
            emailRepository.save(message);
            
            // 3. 발송 채널 결정
            var channel = channelRouter.selectChannel(message);
            
            // 4. 전략 패턴으로 발송
            var strategy = strategyFactory.getStrategy(channel);
            MessageResult result = strategy.send(message);
            
            // 5. 상태 업데이트
            emailRepository.updateStatus(message.getMessageId(), result.getStatus());
            
            log.info("Email sent successfully: {} via {}", message.getMessageId(), channel);
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send email: {}", message.getMessageId(), e);
            emailRepository.updateStatus(message.getMessageId(), MessageStatus.FAILED);
            return MessageResult.failure(message.getMessageId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public MessageStatus getEmailStatus(String messageId) {
        return emailRepository.findById(messageId)
            .map(EmailMessage::getStatus)
            .orElse(MessageStatus.FAILED);
    }
    
    @Transactional
    public void retryEmail(String messageId) {
        emailRepository.findById(messageId)
            .ifPresent(message -> {
                emailRepository.incrementRetryCount(messageId);
                sendEmail(message);
            });
    }
}