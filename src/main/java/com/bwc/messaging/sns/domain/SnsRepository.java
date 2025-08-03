package com.bwc.messaging.sns.domain;

import java.util.List;
import java.util.Optional;

import com.bwc.messaging.shared.domain.MessageStatus;

/**
 * SNS 도메인 리포지토리 인터페이스
 * Infrastructure 계층에서 구현
 */
public interface SnsRepository {
    
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
     * 재전송 횟수 증가
     */
    void incrementRetryCount(String messageId);
    
    /**
     * 발송 대상 메시지 조회 (스케줄링용)
     */
    List<SnsMessage> findPendingMessages(int limit);
    
    /**
     * 채널별 메시지 조회
     */
    List<SnsMessage> findByChannelId(String channelId, int limit);
    
    /**
     * 헬스 체크
     */
    void healthCheck();
}