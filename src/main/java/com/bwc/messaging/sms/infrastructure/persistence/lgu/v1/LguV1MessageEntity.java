package com.bwc.messaging.sms.infrastructure.persistence.lgu.v1;

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
 * LGU V1 발송사 전용 SMS 메시지 엔티티
 */
@Entity
@Table(name = "TB_LGU_V1_SMS_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LguV1MessageEntity {

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

    // LGU V1 전용 필드들
    @Column(name = "LGU_V1_MSG_ID", length = 50)
    private String lguV1MessageId;

    @Column(name = "CALLBACK_NUMBER", length = 20)
    private String callbackNumber;

    @Column(name = "SERVICE_TYPE", length = 10)
    private String serviceType; // SMS, LMS, MMS

    @Column(name = "TEMPLATE_ID", length = 30)
    private String templateId;

    @Column(name = "AD_FLAG", length = 1)
    @Builder.Default
    private String adFlag = "N"; // 광고 여부

    @Column(name = "RESERVE_DATE", length = 8)
    private String reserveDate; // 예약발송일 (YYYYMMDD)

    @Column(name = "RESERVE_TIME", length = 6)
    private String reserveTime; // 예약발송시간 (HHMMSS)

    @Column(name = "REPORT_FLAG", length = 1)
    @Builder.Default
    private String reportFlag = "Y"; // 리포트 수신 여부

    @Column(name = "RETRY_COUNT", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "ERROR_CODE", length = 10)
    private String errorCode;

    @Column(name = "ERROR_MESSAGE", length = 500)
    private String errorMessage;

    // LGU V1 응답 필드들
    @Column(name = "LGU_RESULT_CODE", length = 10)
    private String lguResultCode;

    @Column(name = "LGU_RESULT_MESSAGE", length = 200)
    private String lguResultMessage;

    @Column(name = "LGU_MESSAGE_KEY", length = 50)
    private String lguMessageKey;

    // 시간 관련 필드들
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt;

    @Column(name = "DELIVERED_AT")
    private LocalDateTime deliveredAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}