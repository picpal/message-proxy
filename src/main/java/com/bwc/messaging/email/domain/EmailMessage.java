package com.bwc.messaging.email.domain;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.bwc.messaging.shared.domain.Message;
import com.bwc.messaging.shared.domain.MessageType;

public class EmailMessage extends Message {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private String subject;
    private String htmlContent;
    private List<String> ccReceivers;
    private List<String> bccReceivers;
    private List<EmailAttachment> attachments;
    private boolean isHtml;
    
    public EmailMessage() {
        super(MessageType.EMAIL);
    }
    
    @Override
    public boolean isValid() {
        return EMAIL_PATTERN.matcher(receiver).matches() &&
               EMAIL_PATTERN.matcher(sender).matches() &&
               subject != null && !subject.trim().isEmpty() &&
               content != null && !content.trim().isEmpty();
    }
    
    @Override
    public Map<String, Object> toSendParameters() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("from", sender);
        params.put("to", receiver);
        params.put("subject", subject);
        params.put("content", content);
        params.put("htmlContent", htmlContent);
        params.put("isHtml", isHtml);
        params.put("cc", ccReceivers);
        params.put("bcc", bccReceivers);
        params.put("attachments", attachments);
        return params;
    }
    
    // Builder pattern
    public static EmailMessageBuilder builder() {
        return new EmailMessageBuilder();
    }
    
    public static class EmailMessageBuilder {
        private EmailMessage message = new EmailMessage();
        
        public EmailMessageBuilder messageId(String messageId) {
            message.messageId = messageId;
            return this;
        }
        
        public EmailMessageBuilder from(String sender) {
            message.sender = sender;
            return this;
        }
        
        public EmailMessageBuilder to(String receiver) {
            message.receiver = receiver;
            return this;
        }
        
        public EmailMessageBuilder subject(String subject) {
            message.subject = subject;
            return this;
        }
        
        public EmailMessageBuilder content(String content) {
            message.content = content;
            return this;
        }
        
        public EmailMessageBuilder htmlContent(String htmlContent) {
            message.htmlContent = htmlContent;
            message.isHtml = true;
            return this;
        }
        
        public EmailMessageBuilder cc(List<String> ccReceivers) {
            message.ccReceivers = ccReceivers;
            return this;
        }
        
        public EmailMessageBuilder attachments(List<EmailAttachment> attachments) {
            message.attachments = attachments;
            return this;
        }
        
        public EmailMessage build() {
            return message;
        }
    }
    
    // Getters
    public String getSubject() { return subject; }
    public String getHtmlContent() { return htmlContent; }
    public List<String> getCcReceivers() { return ccReceivers; }
    public List<String> getBccReceivers() { return bccReceivers; }
    public List<EmailAttachment> getAttachments() { return attachments; }
    public boolean isHtml() { return isHtml; }
}