package com.bwc.messaging.sms.application.port.in;

import com.bwc.messaging.shared.domain.MessageResult;

/**
 * SMS 발송 Use Case
 */
public interface SendSmsUseCase {
    
    /**
     * SMS 메시지 발송
     */
    MessageResult sendSms(SendSmsCommand command);
}