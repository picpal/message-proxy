package com.bwc.messaging.sns.domain;

/**
 * SNS 메시지 버튼 (Discord Action Rows, Slack Interactive Components 등)
 */
public class SnsButton {
    
    public enum ButtonType {
        LINK,       // URL 링크 버튼
        CALLBACK,   // 콜백 버튼
        PRIMARY,    // 기본 버튼
        SECONDARY,  // 보조 버튼
        SUCCESS,    // 성공 버튼
        DANGER      // 위험 버튼
    }
    
    private String label;
    private String url;
    private String callbackData;
    private ButtonType type;
    private boolean disabled;
    
    private SnsButton(Builder builder) {
        this.label = builder.label;
        this.url = builder.url;
        this.callbackData = builder.callbackData;
        this.type = builder.type;
        this.disabled = builder.disabled;
    }
    
    public SnsButton(String label, String url) {
        this.label = label;
        this.url = url;
        this.type = ButtonType.LINK;
        this.disabled = false;
    }
    
    public SnsButton(String label, String callbackData, ButtonType type) {
        this.label = label;
        this.callbackData = callbackData;
        this.type = type;
        this.disabled = false;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public boolean isValid() {
        if (label == null || label.trim().isEmpty()) {
            return false;
        }
        
        return switch (type) {
            case LINK -> url != null && !url.trim().isEmpty();
            case CALLBACK, PRIMARY, SECONDARY, SUCCESS, DANGER -> 
                callbackData != null && !callbackData.trim().isEmpty();
        };
    }
    
    // Getters
    public String getLabel() { return label; }
    public String getUrl() { return url; }
    public String getCallbackData() { return callbackData; }
    public ButtonType getType() { return type; }
    public boolean isDisabled() { return disabled; }
    
    // Setters
    public void setDisabled(boolean disabled) { this.disabled = disabled; }
    
    public static class Builder {
        private String label;
        private String url;
        private String callbackData;
        private ButtonType type;
        private boolean disabled = false;
        
        public Builder label(String label) {
            this.label = label;
            return this;
        }
        
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        public Builder callbackData(String callbackData) {
            this.callbackData = callbackData;
            return this;
        }
        
        public Builder type(ButtonType type) {
            this.type = type;
            return this;
        }
        
        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }
        
        public SnsButton build() {
            return new SnsButton(this);
        }
    }
}