package com.bwc.messaging.sns.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * SNS 상태 조회 쿼리 객체
 */
@Getter
@Builder
@RequiredArgsConstructor
public class GetSnsStatusQuery {
    
    private final String messageId;
}