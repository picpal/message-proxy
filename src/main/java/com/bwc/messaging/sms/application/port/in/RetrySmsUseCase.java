package com.bwc.messaging.sms.application.port.in;

/**
 * SMS 재전송 Use Case
 */
public interface RetrySmsUseCase {
    
    /**
     * SMS 메시지 재전송
     */
    void retrySms(RetrySmsCommand command);
}