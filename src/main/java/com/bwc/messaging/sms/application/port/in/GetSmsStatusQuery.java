package com.bwc.messaging.sms.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * SMS 상태 조회 쿼리 객체
 */
@Getter
@Builder
@RequiredArgsConstructor
public class GetSmsStatusQuery {
    
    private final String messageId;
}