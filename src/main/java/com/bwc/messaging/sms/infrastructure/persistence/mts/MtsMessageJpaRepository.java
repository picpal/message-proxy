package com.bwc.messaging.sms.infrastructure.persistence.mts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bwc.messaging.shared.domain.MessageStatus;

@Repository
public interface MtsMessageJpaRepository extends JpaRepository<MtsMessageEntity, Long> {
    
    /**
     * 메시지 ID로 조회
     */
    Optional<MtsMessageEntity> findByMessageId(String messageId);
    
    /**
     * MTS 메시지 ID로 조회
     */
    Optional<MtsMessageEntity> findByMtsMessageId(String mtsMessageId);
    
    /**
     * 발송 대기 중인 메시지 조회 (예약 발송 고려)
     */
    @Query("SELECT m FROM MtsMessageEntity m WHERE m.status = :status " +
           "AND (m.reserveYn = 'N' OR " +
           "(m.reserveYn = 'Y' AND m.reserveDt <= :currentDateTime)) " +
           "ORDER BY m.createdAt ASC")
    List<MtsMessageEntity> findPendingMessages(
        @Param("status") MessageStatus status,
        @Param("currentDateTime") String currentDateTime
    );
    
    /**
     * 재시도 대상 메시지 조회
     */
    @Query("SELECT m FROM MtsMessageEntity m WHERE m.status = :status " +
           "AND m.retryCount < 3 ORDER BY m.updatedAt ASC")
    List<MtsMessageEntity> findRetryTargetMessages(@Param("status") MessageStatus status);
    
    /**
     * 회사별 발송 통계
     */
    @Query("SELECT m.companyId, m.status, COUNT(m) FROM MtsMessageEntity m " +
           "WHERE m.createdAt >= :startDate AND m.createdAt < :endDate " +
           "GROUP BY m.companyId, m.status")
    List<Object[]> findCompanyStatistics(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}