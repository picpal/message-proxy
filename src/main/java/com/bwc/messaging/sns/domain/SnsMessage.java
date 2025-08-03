package com.bwc.messaging.sns.domain;

import java.util.List;
import java.util.Map;

import com.bwc.messaging.shared.domain.Message;
import com.bwc.messaging.shared.domain.MessageType;

public class SnsMessage extends Message {
    
    private String channelId;        // Discord 채널 ID, Slack 채널 등
    private String webhookUrl;       // Webhook URL
    private String title;           // 메시지 제목
    private String description;     // 메시지 설명
    private String color;           // 메시지 색상 (Discord Embed)
    private String thumbnailUrl;    // 썸네일 이미지 URL
    private String imageUrl;        // 첨부 이미지 URL
    private List<SnsField> fields;  // 추가 필드들 (Embed fields)
    private List<SnsButton> buttons; // 버튼들 (Discord, Slack 등)
    private boolean isEmbed;        // Embed 형태 여부
    
    public SnsMessage(MessageType snsType) {
        super(snsType);
        if (!snsType.isSnsType()) {
            throw new IllegalArgumentException("Invalid SNS type: " + snsType);
        }
    }
    
    @Override
    public boolean isValid() {
        // 기본 검증
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        // SNS 타입별 검증
        return switch (type) {
            case KAKAO_TALK, KAKAO_ALIM_TALK, KAKAO_FRIEND_TALK -> 
                receiver != null && !receiver.trim().isEmpty();
            case LINE -> 
                receiver != null && !receiver.trim().isEmpty();
            case FACEBOOK_MESSENGER -> 
                receiver != null && !receiver.trim().isEmpty();
            default -> 
                (channelId != null && !channelId.trim().isEmpty()) || 
                (webhookUrl != null && !webhookUrl.trim().isEmpty());
        };
    }
    
    @Override
    public Map<String, Object> toSendParameters() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("messageId", messageId);
        params.put("type", type.name());
        params.put("sender", sender);
        params.put("receiver", receiver);
        params.put("content", content);
        params.put("channelId", channelId);
        params.put("webhookUrl", webhookUrl);
        params.put("title", title);
        params.put("description", description);
        params.put("color", color);
        params.put("thumbnailUrl", thumbnailUrl);
        params.put("imageUrl", imageUrl);
        params.put("fields", fields);
        params.put("buttons", buttons);
        params.put("isEmbed", isEmbed);
        return params;
    }
    
    // Discord 메시지 생성
    public static SnsMessage createDiscordMessage() {
        SnsMessage message = new SnsMessage(MessageType.FACEBOOK_MESSENGER); // Discord는 임시로 Facebook으로
        message.isEmbed = true;
        return message;
    }
    
    // Slack 메시지 생성  
    public static SnsMessage createSlackMessage() {
        return new SnsMessage(MessageType.LINE); // Slack은 임시로 Line으로
    }
    
    // Builder pattern
    public static SnsMessageBuilder builder(MessageType snsType) {
        return new SnsMessageBuilder(snsType);
    }
    
    public static class SnsMessageBuilder {
        private SnsMessage message;
        
        public SnsMessageBuilder(MessageType snsType) {
            this.message = new SnsMessage(snsType);
        }
        
        public SnsMessageBuilder messageId(String messageId) {
            message.messageId = messageId;
            return this;
        }
        
        public SnsMessageBuilder sender(String sender) {
            message.sender = sender;
            return this;
        }
        
        public SnsMessageBuilder receiver(String receiver) {
            message.receiver = receiver;
            return this;
        }
        
        public SnsMessageBuilder content(String content) {
            message.content = content;
            return this;
        }
        
        public SnsMessageBuilder channelId(String channelId) {
            message.channelId = channelId;
            return this;
        }
        
        public SnsMessageBuilder webhookUrl(String webhookUrl) {
            message.webhookUrl = webhookUrl;
            return this;
        }
        
        public SnsMessageBuilder title(String title) {
            message.title = title;
            return this;
        }
        
        public SnsMessageBuilder description(String description) {
            message.description = description;
            return this;
        }
        
        public SnsMessageBuilder color(String color) {
            message.color = color;
            return this;
        }
        
        public SnsMessageBuilder thumbnailUrl(String thumbnailUrl) {
            message.thumbnailUrl = thumbnailUrl;
            return this;
        }
        
        public SnsMessageBuilder imageUrl(String imageUrl) {
            message.imageUrl = imageUrl;
            return this;
        }
        
        public SnsMessageBuilder fields(List<SnsField> fields) {
            message.fields = fields;
            return this;
        }
        
        public SnsMessageBuilder buttons(List<SnsButton> buttons) {
            message.buttons = buttons;
            return this;
        }
        
        public SnsMessageBuilder asEmbed(boolean isEmbed) {
            message.isEmbed = isEmbed;
            return this;
        }
        
        public SnsMessage build() {
            return message;
        }
    }
    
    // Getters
    public String getChannelId() { return channelId; }
    public String getWebhookUrl() { return webhookUrl; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getImageUrl() { return imageUrl; }
    public List<SnsField> getFields() { return fields; }
    public List<SnsButton> getButtons() { return buttons; }
    public boolean isEmbed() { return isEmbed; }
    
    // Setters for test compatibility
    public void setIsEmbed(boolean isEmbed) { this.isEmbed = isEmbed; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setColor(String color) { this.color = color; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setFields(List<SnsField> fields) { this.fields = fields; }
    public void setButtons(List<SnsButton> buttons) { this.buttons = buttons; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    public void setContent(String content) { this.content = content; }
}