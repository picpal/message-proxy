package com.bwc.messaging.email.application.strategy;

import com.bwc.messaging.email.domain.EmailMessage;
import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;

public interface EmailStrategy {
    
    MessageResult send(EmailMessage message);
    
    MessageChannel getSupportedChannel();
    
    boolean isHealthy();
}