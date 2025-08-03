package com.bwc.messaging.sns.application.strategy;

import com.bwc.messaging.shared.application.MessageSender;
import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.sns.domain.SnsMessage;

/**
 * SNS 발송 전략 인터페이스
 */
public interface SnsStrategy extends MessageSender<SnsMessage> {
    
    /**
     * 지원하는 채널 반환
     */
    MessageChannel getSupportedChannel();
    
    /**
     * 채널 연결 상태 확인
     */
    boolean isHealthy();
}