package com.bwc.messaging.email.domain;

public class EmailAttachment {
    
    private String fileName;
    private String contentType;
    private byte[] content;
    private long size;
    
    public EmailAttachment(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
        this.size = content != null ? content.length : 0;
    }
    
    public boolean isValid() {
        return fileName != null && !fileName.trim().isEmpty() &&
               contentType != null && !contentType.trim().isEmpty() &&
               content != null && content.length > 0 &&
               size <= 25 * 1024 * 1024; // 25MB 제한
    }
    
    // Getters
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public byte[] getContent() { return content; }
    public long getSize() { return size; }
}