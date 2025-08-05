package com.bwc.messaging.sns.application.port.in;

import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sns.domain.SnsMessage;
import com.bwc.messaging.sns.domain.SnsField;
import com.bwc.messaging.sns.domain.SnsButton;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * SNS 발송 명령 객체
 */
@Getter
@Builder
@RequiredArgsConstructor
public class SendSnsCommand {
    
    private final String messageId;
    private final MessageType type;
    private final String sender;
    private final String receiver;
    private final String content;
    private final String webhookUrl;
    private final boolean isEmbed;
    private final String title;
    private final String description;
    private final String color;
    private final List<SnsField> fields;
    private final List<SnsButton> buttons;
    
    /**
     * Command를 Domain 객체로 변환
     */
    public SnsMessage toMessage() {
        return SnsMessage.builder(type)
            .messageId(messageId)
            .sender(sender)
            .receiver(receiver)
            .content(content)
            .webhookUrl(webhookUrl)
            .asEmbed(isEmbed)
            .title(title)
            .description(description)
            .color(color)
            .fields(fields)
            .buttons(buttons)
            .build();
    }
    
    /**
     * Domain 객체에서 Command 생성
     */
    public static SendSnsCommand from(SnsMessage message) {
        return SendSnsCommand.builder()
            .messageId(message.getMessageId())
            .type(message.getType())
            .sender(message.getSender())
            .receiver(message.getReceiver())
            .content(message.getContent())
            .webhookUrl(message.getWebhookUrl())
            .isEmbed(message.isEmbed())
            .title(message.getTitle())
            .description(message.getDescription())
            .color(message.getColor())
            .fields(message.getFields())
            .buttons(message.getButtons())
            .build();
    }
}