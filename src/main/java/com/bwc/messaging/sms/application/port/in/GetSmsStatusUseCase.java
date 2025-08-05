package com.bwc.messaging.sms.application.port.in;

import com.bwc.messaging.shared.domain.MessageStatus;

/**
 * SMS 상태 조회 Use Case
 */
public interface GetSmsStatusUseCase {
    
    /**
     * SMS 메시지 상태 조회
     */
    MessageStatus getSmsStatus(GetSmsStatusQuery query);
}