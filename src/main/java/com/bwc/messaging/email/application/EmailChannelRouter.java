package com.bwc.messaging.email.application;

import org.springframework.stereotype.Component;

import com.bwc.messaging.email.domain.EmailMessage;
import com.bwc.messaging.shared.domain.MessageChannel;

/**
 * Email 발송 채널 선택 로직
 */
@Component
public class EmailChannelRouter {
    
    public MessageChannel selectChannel(EmailMessage message) {
        // 이메일 도메인이나 설정에 따라 발송 채널 결정
        
        if (isGmailMessage(message)) {
            return MessageChannel.GMAIL_API; // Gmail API
        }
        
        if (isOfficeMessage(message)) {
            return MessageChannel.SMTP; // Outlook SMTP
        }
        
        // 기본 SMTP 사용
        return MessageChannel.SMTP;
    }
    
    private boolean isGmailMessage(EmailMessage message) {
        return message.getSender() != null && message.getSender().endsWith("@gmail.com");
    }
    
    private boolean isOfficeMessage(EmailMessage message) {
        return message.getSender() != null && 
               (message.getSender().endsWith("@outlook.com") || 
                message.getSender().endsWith("@hotmail.com"));
    }
    
    /**
     * 채널 장애 시 대체 채널 선택
     */
    public MessageChannel selectFallbackChannel(MessageChannel failedChannel) {
        return switch (failedChannel) {
            case GMAIL_API -> MessageChannel.SMTP;
            case SMTP -> MessageChannel.AWS_SES;
            case AWS_SES -> MessageChannel.SENDGRID;
            default -> MessageChannel.SMTP;
        };
    }
}