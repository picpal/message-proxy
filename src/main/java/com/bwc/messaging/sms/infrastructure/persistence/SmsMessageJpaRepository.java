package com.bwc.messaging.sms.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bwc.messaging.shared.domain.MessageStatus;

@Repository
public interface SmsMessageJpaRepository extends JpaRepository<SmsMessageEntity, Long> {
    
    /**
     * 메시지 ID로 조회
     */
    Optional<SmsMessageEntity> findByMessageId(String messageId);
    
    /**
     * 발송 대기 중인 메시지 조회
     */
    @Query("SELECT s FROM SmsMessageEntity s WHERE s.status = :status ORDER BY s.createdAt ASC")
    List<SmsMessageEntity> findByStatusOrderByCreatedAtAsc(@Param("status") MessageStatus status);
    
    /**
     * 재시도 대상 메시지 조회 (실패했지만 재시도 횟수가 제한 미만)
     */
    @Query("SELECT s FROM SmsMessageEntity s WHERE s.status = :status AND s.retryCount < :maxRetryCount ORDER BY s.createdAt ASC")
    List<SmsMessageEntity> findRetryTargetMessages(
        @Param("status") MessageStatus status, 
        @Param("maxRetryCount") int maxRetryCount
    );
    
    /**
     * 특정 기간의 메시지 통계 조회
     */
    @Query("SELECT s.status, COUNT(s) FROM SmsMessageEntity s WHERE s.createdAt >= :startDate AND s.createdAt < :endDate GROUP BY s.status")
    List<Object[]> findMessageStatsByDateRange(
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate
    );
}