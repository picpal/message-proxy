package com.bwc.messaging.email.application.strategy.impl;

import org.springframework.stereotype.Component;

import com.bwc.messaging.email.application.strategy.EmailStrategy;
import com.bwc.messaging.email.domain.EmailMessage;
import com.bwc.messaging.email.infrastructure.external.SmtpEmailClient;
import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailStrategy implements EmailStrategy {
    
    private final SmtpEmailClient smtpClient;
    
    @Override
    public MessageResult send(EmailMessage message) {
        log.info("Sending email via SMTP: {}", message.getMessageId());
        
        try {
            var response = smtpClient.sendEmail(message);
            
            if (response.isSuccess()) {
                return MessageResult.success(message.getMessageId(), response.getMessageId());
            } else {
                return MessageResult.failure(message.getMessageId(), 
                    response.getErrorCode(), response.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("SMTP send failed: {}", message.getMessageId(), e);
            return MessageResult.failure(message.getMessageId(), "SMTP_ERROR", e.getMessage());
        }
    }
    
    @Override
    public MessageChannel getSupportedChannel() {
        return MessageChannel.SMTP;
    }
    
    @Override
    public boolean isHealthy() {
        return smtpClient.healthCheck();
    }
}