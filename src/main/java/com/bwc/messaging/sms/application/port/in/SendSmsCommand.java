package com.bwc.messaging.sms.application.port.in;

import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.domain.SmsMessage;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * SMS 발송 명령 객체
 */
@Getter
@Builder
@RequiredArgsConstructor
public class SendSmsCommand {
    
    private final String messageId;
    private final MessageType type;
    private final String sender;
    private final String senderNumber;
    private final String receiver;
    private final String content;
    
    /**
     * Command를 Domain 객체로 변환
     */
    public SmsMessage toMessage() {
        return SmsMessage.builder(type)
            .messageId(messageId)
            .sender(sender)
            .senderNumber(senderNumber)
            .receiver(receiver)
            .content(content)
            .build();
    }
    
    /**
     * Domain 객체에서 Command 생성
     */
    public static SendSmsCommand from(SmsMessage message) {
        return SendSmsCommand.builder()
            .messageId(message.getMessageId())
            .type(message.getType())
            .sender(message.getSender())
            .senderNumber(message.getSenderNumber())
            .receiver(message.getReceiver())
            .content(message.getContent())
            .build();
    }
}