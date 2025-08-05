package com.bwc.messaging.email.application.port.in;

import com.bwc.messaging.shared.domain.MessageStatus;

/**
 * Email 상태 조회 Use Case
 */
public interface GetEmailStatusUseCase {
    
    /**
     * Email 메시지 상태 조회
     */
    MessageStatus getEmailStatus(GetEmailStatusQuery query);
}