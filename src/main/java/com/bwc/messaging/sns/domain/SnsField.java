package com.bwc.messaging.sns.domain;

/**
 * SNS 메시지 필드 (Discord Embed Fields, Slack Attachments 등)
 */
public class SnsField {
    
    private String name;
    private String value;
    private boolean inline;
    
    private SnsField(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.inline = builder.inline;
    }
    
    public SnsField(String name, String value) {
        this(name, value, false);
    }
    
    public SnsField(String name, String value, boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               value != null && !value.trim().isEmpty();
    }
    
    // Getters
    public String getName() { return name; }
    public String getValue() { return value; }
    public boolean isInline() { return inline; }
    
    public static class Builder {
        private String name;
        private String value;
        private boolean inline = false;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder value(String value) {
            this.value = value;
            return this;
        }
        
        public Builder inline(boolean inline) {
            this.inline = inline;
            return this;
        }
        
        public SnsField build() {
            return new SnsField(this);
        }
    }
}