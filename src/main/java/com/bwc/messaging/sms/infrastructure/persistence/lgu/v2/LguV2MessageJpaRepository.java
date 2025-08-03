package com.bwc.messaging.sms.infrastructure.persistence.lgu.v2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bwc.messaging.shared.domain.MessageStatus;

@Repository
public interface LguV2MessageJpaRepository extends JpaRepository<LguV2MessageEntity, Long> {
    
    /**
     * 메시지 ID로 조회
     */
    Optional<LguV2MessageEntity> findByMessageId(String messageId);
    
    /**
     * LGU V2 트랜잭션 ID로 조회
     */
    Optional<LguV2MessageEntity> findByLguV2TransactionId(String lguV2TransactionId);
    
    /**
     * 발송 대기 중인 메시지 조회 (예약 시간 고려)
     */
    @Query("SELECT l FROM LguV2MessageEntity l WHERE l.status = :status " +
           "AND (l.scheduledAt IS NULL OR l.scheduledAt <= :currentTime) " +
           "ORDER BY l.createdAt ASC")
    List<LguV2MessageEntity> findPendingMessages(
        @Param("status") MessageStatus status,
        @Param("currentTime") LocalDateTime currentTime
    );
    
    /**
     * 재시도 대상 메시지 조회
     */
    @Query("SELECT l FROM LguV2MessageEntity l WHERE l.status = :status " +
           "AND l.retryCount < l.maxRetryCount ORDER BY l.updatedAt ASC")
    List<LguV2MessageEntity> findRetryTargetMessages(@Param("status") MessageStatus status);
}