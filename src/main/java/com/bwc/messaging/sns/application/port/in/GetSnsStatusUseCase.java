package com.bwc.messaging.sns.application.port.in;

import com.bwc.messaging.shared.domain.MessageStatus;

/**
 * SNS 상태 조회 Use Case
 */
public interface GetSnsStatusUseCase {
    
    /**
     * SNS 메시지 상태 조회
     */
    MessageStatus getSnsStatus(GetSnsStatusQuery query);
}