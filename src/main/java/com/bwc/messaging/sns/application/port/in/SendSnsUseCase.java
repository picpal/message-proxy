package com.bwc.messaging.sns.application.port.in;

import com.bwc.messaging.shared.domain.MessageResult;

/**
 * SNS 발송 Use Case
 */
public interface SendSnsUseCase {
    
    /**
     * SNS 메시지 발송
     */
    MessageResult sendSns(SendSnsCommand command);
}