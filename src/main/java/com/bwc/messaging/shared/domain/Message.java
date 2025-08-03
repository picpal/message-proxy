package com.bwc.messaging.shared.domain;

import java.time.LocalDateTime;
import java.util.Map;

public abstract class Message {
    
    protected String messageId;
    protected MessageType type;
    protected String sender;
    protected String receiver;
    protected String content;
    protected MessageStatus status;
    protected MessageChannel channel;
    protected LocalDateTime createdAt;
    protected LocalDateTime sentAt;
    protected Map<String, Object> metadata;
    
    protected Message(MessageType type) {
        this.type = type;
        this.status = MessageStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    // 추상 메서드 - 각 타입별로 구현 필요
    public abstract boolean isValid();
    public abstract Map<String, Object> toSendParameters();
    
    // 공통 메서드
    public void markAsSent() {
        this.status = MessageStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = MessageStatus.FAILED;
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put("failureReason", reason);
    }
    
    public void setStatus(MessageStatus status) {
        this.status = status;
    }
    
    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public MessageType getType() { return type; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public MessageStatus getStatus() { return status; }
    public MessageChannel getChannel() { return channel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public Map<String, Object> getMetadata() { return metadata; }
}