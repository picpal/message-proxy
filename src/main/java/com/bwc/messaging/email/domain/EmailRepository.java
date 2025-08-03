package com.bwc.messaging.email.domain;

import java.util.Optional;

import com.bwc.messaging.shared.domain.MessageStatus;

public interface EmailRepository {
    
    void save(EmailMessage message);
    
    Optional<EmailMessage> findById(String messageId);
    
    void updateStatus(String messageId, MessageStatus status);
    
    void incrementRetryCount(String messageId);
}