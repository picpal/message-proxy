package com.bwc.messaging.email.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Email 상태 조회 쿼리 객체
 */
@Getter
@Builder
@RequiredArgsConstructor
public class GetEmailStatusQuery {
    
    private final String messageId;
}