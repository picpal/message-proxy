package com.bwc.messaging.sms.infrastructure.persistence.lgu.v2;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;

/**
 * LGU V2 발송사 전용 SMS 메시지 엔티티
 */
@Entity
@Table(name = "TB_LGU_V2_SMS_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LguV2MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공통 필드들
    @Column(name = "MESSAGE_ID", unique = true, nullable = false, length = 50)
    private String messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "MESSAGE_TYPE", nullable = false)
    private MessageType messageType;

    @Column(name = "SENDER", length = 100)
    private String sender;

    @Column(name = "SENDER_NUMBER", length = 20)
    private String senderNumber;

    @Column(name = "RECEIVER", nullable = false, length = 20)
    private String receiver;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "SUBJECT", length = 200)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private MessageStatus status;

    // LGU V2 전용 필드들 (V2는 REST API 방식)
    @Column(name = "LGU_V2_TRANSACTION_ID", length = 100)
    private String lguV2TransactionId;

    @Column(name = "ACCESS_TOKEN", length = 500)
    private String accessToken;

    @Column(name = "API_VERSION", length = 10)
    @Builder.Default
    private String apiVersion = "2.0";

    @Column(name = "CONTENT_TYPE", length = 20)
    @Builder.Default
    private String contentType = "SMS"; // SMS, LMS, MMS, RCS

    @Column(name = "PRIORITY", length = 10)
    @Builder.Default
    private String priority = "NORMAL"; // HIGH, NORMAL, LOW

    @Column(name = "TTL", nullable = false)
    @Builder.Default
    private Integer ttl = 3600; // Time To Live (초)

    @Column(name = "DELIVERY_RECEIPT", length = 1)
    @Builder.Default
    private String deliveryReceipt = "Y";

    @Column(name = "WEBHOOK_URL", length = 500)
    private String webhookUrl;

    @Column(name = "CUSTOM_FIELDS", columnDefinition = "JSON")
    private String customFields; // JSON 형태의 확장 필드

    @Column(name = "RETRY_COUNT", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "MAX_RETRY_COUNT", nullable = false)
    @Builder.Default
    private Integer maxRetryCount = 3;

    // LGU V2 응답 필드들
    @Column(name = "LGU_RESPONSE_CODE", length = 10)
    private String lguResponseCode;

    @Column(name = "LGU_RESPONSE_MESSAGE", length = 500)
    private String lguResponseMessage;

    @Column(name = "LGU_TRACKING_ID", length = 100)
    private String lguTrackingId;

    @Column(name = "LGU_BATCH_ID", length = 50)
    private String lguBatchId;

    // 시간 관련 필드들
    @Column(name = "SCHEDULED_AT")
    private LocalDateTime scheduledAt; // 예약 발송 시간

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt;

    @Column(name = "DELIVERED_AT")
    private LocalDateTime deliveredAt;

    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}