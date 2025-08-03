package com.bwc.messaging.shared.domain;

public enum MessageStatus {
    PENDING("발송대기"),
    QUEUED("큐대기"),
    SENDING("발송중"),
    SENT("발송완료"),
    DELIVERED("전달완료"),
    READ("읽음완료"),
    FAILED("발송실패"),
    CANCELLED("발송취소"),
    EXPIRED("발송만료");
    
    private final String description;
    
    MessageStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isCompleted() {
        return this == SENT || this == DELIVERED || this == READ;
    }
    
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED || this == EXPIRED;
    }
    
    public boolean isPending() {
        return this == PENDING || this == QUEUED || this == SENDING;
    }
}