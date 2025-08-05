package com.bwc.messaging.email.application.port.out;

import java.util.Optional;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.email.domain.EmailMessage;

/**
 * Email Repository 포트 인터페이스
 */
public interface EmailRepositoryPort {
    
    /**
     * Email 메시지 저장
     */
    void save(EmailMessage message);
    
    /**
     * Email 메시지 조회
     */
    Optional<EmailMessage> findById(String messageId);
    
    /**
     * Email 메시지 상태 업데이트
     */
    void updateStatus(String messageId, MessageStatus status);
    
    /**
     * 헬스 체크 (DB 연결 상태 확인)
     */
    void healthCheck();
}