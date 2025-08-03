package com.bwc.messaging.shared.application;

import com.bwc.messaging.shared.domain.Message;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;

/**
 * 메시지 발송을 위한 공통 인터페이스
 * 모든 메시지 타입(SMS, Email, SNS, Push)에서 구현해야 함
 */
public interface MessageSender<T extends Message> {
    
    /**
     * 메시지 발송
     */
    MessageResult send(T message);
    
    /**
     * 메시지 상태 조회
     */
    MessageStatus getStatus(String messageId);
    
    /**
     * 발송 가능 여부 확인
     */
    boolean canSend(T message);
    
    /**
     * 지원하는 메시지 타입 반환
     */
    Class<T> getSupportedMessageType();
}