package com.bwc.messaging.gateway.presentation.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.domain.SmsMessage;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 통합 메시지 발송 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedMessageRequest {
    
    private String messageId;
    
    @NotNull
    private MessageType type;
    
    @NotEmpty
    private String sender;
    
    @NotEmpty
    private String receiver;
    
    @NotEmpty
    private String content;
    
    // SMS 전용 필드
    private String senderNumber;
    private String templateCode;
    private String subject; // LMS/MMS용
    
    // Email 전용 필드
    private String emailSubject;
    private String htmlContent;
    private List<String> ccReceivers;
    private List<String> bccReceivers;
    private List<EmailAttachmentDto> attachments;
    
    // SNS 전용 필드
    private String channelId;        // Discord 채널 ID, Slack 채널 등
    private String webhookUrl;       // Webhook URL (Discord, Slack 등)
    private String description;      // 메시지 설명
    private String color;           // 메시지 색상 (Discord Embed)
    private String thumbnailUrl;    // 썸네일 이미지 URL
    private String imageUrl;        // 첨부 이미지 URL
    private List<SnsFieldDto> fields; // 추가 필드들 (Embed fields)
    private List<SnsButtonDto> buttons; // 버튼들
    private boolean isEmbed;        // Embed 형태 여부
    private String templateId;      // 카카오 알림톡 템플릿 ID
    private Map<String, String> templateParams; // 템플릿 파라미터
    
    // Push 전용 필드
    private String deviceToken;
    private String title;
    private Map<String, Object> data; // 추가 데이터
    
    // 공통 메타데이터
    private Map<String, Object> metadata;
    
    // 메시지 ID가 없으면 자동 생성
    public String getMessageId() {
        if (messageId == null || messageId.trim().isEmpty()) {
            messageId = generateMessageId();
        }
        return messageId;
    }
    
    private String generateMessageId() {
        return type.name() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    // SMS 메시지로 변환
    public SmsMessage toSmsMessage() {
        if (!type.isSmsType()) {
            throw new IllegalStateException("Cannot convert to SmsMessage: " + type);
        }
        
        return SmsMessage.builder(type)
            .messageId(getMessageId())
            .sender(sender)
            .senderNumber(senderNumber)
            .receiver(receiver)
            .content(content)
            .templateCode(templateCode)
            .subject(subject)
            .build();
    }
    
    // Email 메시지로 변환
    public com.bwc.messaging.email.domain.EmailMessage toEmailMessage() {
        if (type != MessageType.EMAIL) {
            throw new IllegalStateException("Cannot convert to EmailMessage: " + type);
        }
        
        return com.bwc.messaging.email.domain.EmailMessage.builder()
            .messageId(getMessageId())
            .from(sender)
            .to(receiver)
            .subject(emailSubject != null ? emailSubject : subject)
            .content(content)
            .htmlContent(htmlContent)
            .cc(ccReceivers)
            .attachments(attachments != null ? attachments.stream()
                .map(dto -> new com.bwc.messaging.email.domain.EmailAttachment(
                    dto.getFileName(), dto.getContentType(), dto.getContent()))
                .toList() : null)
            .build();
    }
    
    // SNS 메시지로 변환
    public com.bwc.messaging.sns.domain.SnsMessage toSnsMessage() {
        if (!type.isSnsType()) {
            throw new IllegalStateException("Cannot convert to SnsMessage: " + type);
        }
        
        var builder = com.bwc.messaging.sns.domain.SnsMessage.builder(type)
            .messageId(getMessageId())
            .sender(sender)
            .receiver(receiver)
            .content(content)
            .channelId(channelId)
            .webhookUrl(webhookUrl)
            .title(title)
            .description(description)
            .color(color)
            .thumbnailUrl(thumbnailUrl)
            .imageUrl(imageUrl)
            .asEmbed(isEmbed);
        
        // Fields 변환
        if (fields != null && !fields.isEmpty()) {
            List<com.bwc.messaging.sns.domain.SnsField> snsFields = fields.stream()
                .map(dto -> new com.bwc.messaging.sns.domain.SnsField(dto.getName(), dto.getValue(), dto.isInline()))
                .toList();
            builder.fields(snsFields);
        }
        
        // Buttons 변환
        if (buttons != null && !buttons.isEmpty()) {
            List<com.bwc.messaging.sns.domain.SnsButton> snsButtons = buttons.stream()
                .map(dto -> dto.getUrl() != null ? 
                    new com.bwc.messaging.sns.domain.SnsButton(dto.getLabel(), dto.getUrl()) :
                    new com.bwc.messaging.sns.domain.SnsButton(dto.getLabel(), dto.getCallbackData(), 
                        com.bwc.messaging.sns.domain.SnsButton.ButtonType.valueOf(dto.getType().toUpperCase())))
                .toList();
            builder.buttons(snsButtons);
        }
        
        return builder.build();
    }
    
    // Push 메시지로 변환 (추후 구현)
    public Object toPushMessage() {
        // TODO: PushMessage 클래스 구현 후 변환 로직 추가
        throw new UnsupportedOperationException("Push message conversion not implemented yet");
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmailAttachmentDto {
        private String fileName;
        private String contentType;
        private byte[] content;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SnsFieldDto {
        private String name;
        private String value;
        private boolean inline;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SnsButtonDto {
        private String label;
        private String url;
        private String callbackData;
        private String type; // LINK, CALLBACK, PRIMARY, SECONDARY, SUCCESS, DANGER
    }
}