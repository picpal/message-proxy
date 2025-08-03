package com.bwc.messaging.sms.infrastructure.persistence.lgu.v1;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bwc.messaging.shared.domain.MessageStatus;

@Repository
public interface LguV1MessageJpaRepository extends JpaRepository<LguV1MessageEntity, Long> {
    
    /**
     * 메시지 ID로 조회
     */
    Optional<LguV1MessageEntity> findByMessageId(String messageId);
    
    /**
     * LGU V1 메시지 ID로 조회
     */
    Optional<LguV1MessageEntity> findByLguV1MessageId(String lguV1MessageId);
    
    /**
     * 발송 대기 중인 메시지 조회 (예약발송 시간 고려)
     */
    @Query("SELECT l FROM LguV1MessageEntity l WHERE l.status = :status " +
           "AND (l.reserveDate IS NULL OR " +
           "(l.reserveDate <= :currentDate AND l.reserveTime <= :currentTime)) " +
           "ORDER BY l.createdAt ASC")
    List<LguV1MessageEntity> findPendingMessages(
        @Param("status") MessageStatus status,
        @Param("currentDate") String currentDate,
        @Param("currentTime") String currentTime
    );
    
    /**
     * 재시도 대상 메시지 조회
     */
    @Query("SELECT l FROM LguV1MessageEntity l WHERE l.status = :status " +
           "AND l.retryCount < 3 ORDER BY l.updatedAt ASC")
    List<LguV1MessageEntity> findRetryTargetMessages(@Param("status") MessageStatus status);
    
    /**
     * LGU 결과 코드별 통계
     */
    @Query("SELECT l.lguResultCode, COUNT(l) FROM LguV1MessageEntity l " +
           "WHERE l.createdAt >= :startDate AND l.createdAt < :endDate " +
           "GROUP BY l.lguResultCode")
    List<Object[]> findResultCodeStatistics(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * 발송사별 성공률 조회
     */
    @Query("SELECT l.status, COUNT(l) FROM LguV1MessageEntity l " +
           "WHERE l.createdAt >= :startDate AND l.createdAt < :endDate " +
           "GROUP BY l.status")
    List<Object[]> findSuccessRateStatistics(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}