package com.bwc.messaging.email.application.port.in;

import com.bwc.messaging.shared.domain.MessageResult;

/**
 * Email 발송 Use Case
 */
public interface SendEmailUseCase {
    
    /**
     * Email 메시지 발송
     */
    MessageResult sendEmail(SendEmailCommand command);
}