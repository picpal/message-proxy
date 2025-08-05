package com.bwc.messaging.sns.application.port.out;

import java.util.Optional;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sns.domain.SnsMessage;

/**
 * SNS Repository 포트 인터페이스
 */
public interface SnsRepositoryPort {
    
    /**
     * SNS 메시지 저장
     */
    void save(SnsMessage message);
    
    /**
     * SNS 메시지 조회
     */
    Optional<SnsMessage> findById(String messageId);
    
    /**
     * SNS 메시지 상태 업데이트
     */
    void updateStatus(String messageId, MessageStatus status);
    
    /**
     * 헬스 체크 (DB 연결 상태 확인)
     */
    void healthCheck();
}