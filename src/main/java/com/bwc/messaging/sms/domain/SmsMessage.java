package com.bwc.messaging.sms.domain;

import java.util.Map;
import java.util.regex.Pattern;

import com.bwc.messaging.shared.domain.Message;
import com.bwc.messaging.shared.domain.MessageType;

public class SmsMessage extends Message {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,13}$");
    private static final int SMS_MAX_LENGTH = 80;
    private static final int LMS_MAX_LENGTH = 2000;
    
    private String senderNumber;
    private String templateCode;
    private String subject; // LMS/MMS용
    
    public SmsMessage(MessageType smsType) {
        super(smsType);
        if (!smsType.isSmsType()) {
            throw new IllegalArgumentException("Invalid SMS type: " + smsType);
        }
    }
    
    @Override
    public boolean isValid() {
        if (!PHONE_PATTERN.matcher(receiver.replaceAll("[^0-9]", "")).matches()) {
            return false;
        }
        
        if (senderNumber == null || !PHONE_PATTERN.matcher(senderNumber.replaceAll("[^0-9]", "")).matches()) {
            return false;
        }
        
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        // 메시지 타입별 길이 검증
        return switch (type) {
            case SMS -> content.length() <= SMS_MAX_LENGTH;
            case LMS, MMS -> content.length() <= LMS_MAX_LENGTH;
            default -> false;
        };
    }
    
    @Override
    public Map<String, Object> toSendParameters() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("messageId", messageId);
        params.put("type", type.name());
        params.put("sender", sender);
        params.put("senderNumber", senderNumber);
        params.put("receiver", receiver);
        params.put("content", content);
        params.put("templateCode", templateCode);
        
        if (type == MessageType.LMS || type == MessageType.MMS) {
            params.put("subject", subject);
        }
        
        return params;
    }
    
    // SMS 타입 자동 결정
    public static SmsMessage createAutoType(String content) {
        MessageType type = content.length() > SMS_MAX_LENGTH ? MessageType.LMS : MessageType.SMS;
        return new SmsMessage(type);
    }
    
    public static SmsMessage createSms() {
        return new SmsMessage(MessageType.SMS);
    }
    
    public static SmsMessage createLms() {
        return new SmsMessage(MessageType.LMS);
    }
    
    public static SmsMessage createMms() {
        return new SmsMessage(MessageType.MMS);
    }
    
    // Builder pattern
    public static SmsMessageBuilder builder(MessageType smsType) {
        return new SmsMessageBuilder(smsType);
    }
    
    public static class SmsMessageBuilder {
        private SmsMessage message;
        
        public SmsMessageBuilder(MessageType smsType) {
            this.message = new SmsMessage(smsType);
        }
        
        public SmsMessageBuilder messageId(String messageId) {
            message.messageId = messageId;
            return this;
        }
        
        public SmsMessageBuilder sender(String sender) {
            message.sender = sender;
            return this;
        }
        
        public SmsMessageBuilder senderNumber(String senderNumber) {
            message.senderNumber = senderNumber;
            return this;
        }
        
        public SmsMessageBuilder receiver(String receiver) {
            message.receiver = receiver;
            return this;
        }
        
        public SmsMessageBuilder content(String content) {
            message.content = content;
            return this;
        }
        
        public SmsMessageBuilder templateCode(String templateCode) {
            message.templateCode = templateCode;
            return this;
        }
        
        public SmsMessageBuilder subject(String subject) {
            message.subject = subject;
            return this;
        }
        
        public SmsMessage build() {
            return message;
        }
    }
    
    // Getters
    public String getSenderNumber() { return senderNumber; }
    public String getTemplateCode() { return templateCode; }
    public String getSubject() { return subject; }
    
    // Setters for test compatibility
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setSender(String sender) { this.sender = sender; }
    public void setContent(String content) { this.content = content; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public void setType(MessageType type) { this.type = type; }
}