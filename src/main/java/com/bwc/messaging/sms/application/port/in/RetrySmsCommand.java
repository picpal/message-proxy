package com.bwc.messaging.sms.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * SMS 재전송 명령 객체
 */
@Getter
@Builder
@RequiredArgsConstructor
public class RetrySmsCommand {
    
    private final String messageId;
}