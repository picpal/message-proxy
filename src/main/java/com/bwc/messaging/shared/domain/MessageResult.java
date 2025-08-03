package com.bwc.messaging.shared.domain;

import java.time.LocalDateTime;

public class MessageResult {
    
    private final String messageId;
    private final MessageStatus status;
    private final String resultCode;
    private final String resultMessage;
    private final LocalDateTime processedAt;
    private final String vendorMessageId; // 외부 업체에서 제공하는 메시지 ID
    
    private MessageResult(Builder builder) {
        this.messageId = builder.messageId;
        this.status = builder.status;
        this.resultCode = builder.resultCode;
        this.resultMessage = builder.resultMessage;
        this.processedAt = builder.processedAt != null ? builder.processedAt : LocalDateTime.now();
        this.vendorMessageId = builder.vendorMessageId;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static MessageResult success(String messageId, String vendorMessageId) {
        return builder()
            .messageId(messageId)
            .status(MessageStatus.SENT)
            .resultCode("0000")
            .resultMessage("Success")
            .vendorMessageId(vendorMessageId)
            .build();
    }
    
    public static MessageResult failure(String messageId, String errorCode, String errorMessage) {
        return builder()
            .messageId(messageId)
            .status(MessageStatus.FAILED)
            .resultCode(errorCode)
            .resultMessage(errorMessage)
            .build();
    }
    
    public boolean isSuccess() {
        return status == MessageStatus.SENT || status == MessageStatus.DELIVERED;
    }
    
    public boolean isFailure() {
        return status.isFailed();
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public MessageStatus getStatus() { return status; }
    public String getResultCode() { return resultCode; }
    public String getResultMessage() { return resultMessage; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public String getVendorMessageId() { return vendorMessageId; }
    
    // 테스트 호환성을 위한 별칭 메서드들
    public String getErrorCode() { return resultCode; }
    public String getErrorMessage() { return resultMessage; }
    
    public static class Builder {
        private String messageId;
        private MessageStatus status;
        private String resultCode;
        private String resultMessage;
        private LocalDateTime processedAt;
        private String vendorMessageId;
        
        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }
        
        public Builder status(MessageStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder resultCode(String resultCode) {
            this.resultCode = resultCode;
            return this;
        }
        
        public Builder resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }
        
        public Builder processedAt(LocalDateTime processedAt) {
            this.processedAt = processedAt;
            return this;
        }
        
        public Builder vendorMessageId(String vendorMessageId) {
            this.vendorMessageId = vendorMessageId;
            return this;
        }
        
        public MessageResult build() {
            return new MessageResult(this);
        }
    }
}