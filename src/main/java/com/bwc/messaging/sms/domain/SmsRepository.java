package com.bwc.messaging.sms.domain;

import java.util.Optional;

import com.bwc.messaging.shared.domain.MessageStatus;

/**
 * SMS 도메인 리포지토리 인터페이스
 * Infrastructure 계층에서 구현
 */
public interface SmsRepository {
    
    /**
     * SMS 메시지 저장
     */
    void save(SmsMessage message);
    
    /**
     * SMS 메시지 조회
     */
    Optional<SmsMessage> findById(String messageId);
    
    /**
     * SMS 메시지 상태 업데이트
     */
    void updateStatus(String messageId, MessageStatus status);
    
    /**
     * 재전송 횟수 증가
     */
    void incrementRetryCount(String messageId);
    
    /**
     * 발송 대상 메시지 조회 (스케줄링용)
     */
    java.util.List<SmsMessage> findPendingMessages(int limit);
    
    /**
     * 헬스 체크 (DB 연결 상태 확인)
     */
    void healthCheck();
}