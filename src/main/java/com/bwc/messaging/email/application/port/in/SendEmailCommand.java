package com.bwc.messaging.email.application.port.in;

import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.email.domain.EmailMessage;
import com.bwc.messaging.email.domain.EmailAttachment;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Email 발송 명령 객체
 */
@Getter
@Builder
@RequiredArgsConstructor
public class SendEmailCommand {
    
    private final String messageId;
    private final MessageType type;
    private final String sender;
    private final String receiver;
    private final String subject;
    private final String content;
    private final List<EmailAttachment> attachments;
    
    /**
     * Command를 Domain 객체로 변환
     */
    public EmailMessage toMessage() {
        return EmailMessage.builder()
            .messageId(messageId)
            .from(sender)
            .to(receiver)
            .subject(subject)
            .content(content)
            .attachments(attachments)
            .build();
    }
    
    /**
     * Domain 객체에서 Command 생성
     */
    public static SendEmailCommand from(EmailMessage message) {
        return SendEmailCommand.builder()
            .messageId(message.getMessageId())
            .type(message.getType())
            .sender(message.getSender())
            .receiver(message.getReceiver())
            .subject(message.getSubject())
            .content(message.getContent())
            .attachments(message.getAttachments())
            .build();
    }
}