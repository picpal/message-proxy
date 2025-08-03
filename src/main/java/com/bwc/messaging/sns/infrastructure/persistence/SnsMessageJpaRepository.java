package com.bwc.messaging.sns.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageStatus;

@Repository
public interface SnsMessageJpaRepository extends JpaRepository<SnsMessageEntity, Long> {
    
    /**
     * 메시지 ID로 조회
     */
    Optional<SnsMessageEntity> findByMessageId(String messageId);
    
    /**
     * 발송사 메시지 ID로 조회
     */
    Optional<SnsMessageEntity> findByVendorMessageId(String vendorMessageId);
    
    /**
     * 채널별 대기 메시지 조회
     */
    @Query("SELECT s FROM SnsMessageEntity s WHERE s.status = :status AND s.channel = :channel ORDER BY s.createdAt ASC")
    List<SnsMessageEntity> findPendingMessagesByChannel(
        @Param("status") MessageStatus status, 
        @Param("channel") MessageChannel channel
    );
    
    /**
     * 재시도 대상 메시지 조회
     */
    @Query("SELECT s FROM SnsMessageEntity s WHERE s.status = :status AND s.retryCount < 3 ORDER BY s.updatedAt ASC")
    List<SnsMessageEntity> findRetryTargetMessages(@Param("status") MessageStatus status);
    
    /**
     * 채널별 발송 통계
     */
    @Query("SELECT s.channel, s.status, COUNT(s) FROM SnsMessageEntity s " +
           "WHERE s.createdAt >= :startDate AND s.createdAt < :endDate " +
           "GROUP BY s.channel, s.status")
    List<Object[]> findChannelStatistics(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Embed vs 일반 메시지 통계
     */
    @Query("SELECT s.isEmbed, COUNT(s) FROM SnsMessageEntity s " +
           "WHERE s.createdAt >= :startDate AND s.createdAt < :endDate " +
           "GROUP BY s.isEmbed")
    List<Object[]> findEmbedStatistics(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}