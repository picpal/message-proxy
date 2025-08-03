package com.bwc.messaging.sms.infrastructure.persistence.mts;

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
 * MTS 발송사 전용 SMS 메시지 엔티티
 */
@Entity
@Table(name = "TB_MTS_SMS_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MtsMessageEntity {

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

    // MTS 전용 필드들
    @Column(name = "MTS_MESSAGE_ID", length = 50)
    private String mtsMessageId;

    @Column(name = "TELECOM_CODE", length = 10)
    private String telecomCode; // SKT: 01, KT: 02, LGU+: 03

    @Column(name = "MESSAGE_CODE", length = 10)
    private String messageCode; // SMS: 01, LMS: 02, MMS: 03

    @Column(name = "NATION_CODE", length = 5)
    @Builder.Default
    private String nationCode = "82"; // 국가번호

    @Column(name = "DEST_PHONE", length = 20)
    private String destPhone; // MTS 포맷의 수신번호

    @Column(name = "SEND_PHONE", length = 20)
    private String sendPhone; // MTS 포맷의 발신번호

    @Column(name = "MSG_BODY", columnDefinition = "TEXT")
    private String msgBody; // MTS 포맷의 메시지 본문

    @Column(name = "RESERVE_YN", length = 1)
    @Builder.Default
    private String reserveYn = "N"; // 예약발송 여부

    @Column(name = "RESERVE_DT", length = 14)
    private String reserveDt; // 예약일시 (YYYYMMDDHHMISS)

    @Column(name = "REPEAT_YN", length = 1)
    @Builder.Default
    private String repeatYn = "N"; // 반복발송 여부

    @Column(name = "REPORT_YN", length = 1)
    @Builder.Default
    private String reportYn = "Y"; // 결과보고서 수신 여부

    @Column(name = "RETRY_COUNT", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "ERROR_CODE", length = 10)
    private String errorCode;

    @Column(name = "ERROR_MESSAGE", length = 500)
    private String errorMessage;

    // MTS 응답 필드들
    @Column(name = "MTS_RESULT_CODE", length = 10)
    private String mtsResultCode;

    @Column(name = "MTS_RESULT_MESSAGE", length = 300)
    private String mtsResultMessage;

    @Column(name = "MTS_SEQ_NO", length = 20)
    private String mtsSeqNo; // MTS 순번

    @Column(name = "MTS_BATCH_NO", length = 20)
    private String mtsBatchNo; // MTS 배치번호

    // MTS 특화 필드들
    @Column(name = "COMPANY_ID", length = 20)
    private String companyId; // 업체 ID

    @Column(name = "SERVICE_CD", length = 10)
    private String serviceCd; // 서비스 코드

    @Column(name = "BILL_TYPE", length = 5)
    @Builder.Default
    private String billType = "01"; // 과금 유형

    @Column(name = "ETC_NUM_1", length = 20)
    private String etcNum1; // 기타숫자1

    @Column(name = "ETC_NUM_2", length = 20)
    private String etcNum2; // 기타숫자2

    @Column(name = "ETC_STR_1", length = 100)
    private String etcStr1; // 기타문자1

    @Column(name = "ETC_STR_2", length = 100)
    private String etcStr2; // 기타문자2

    // 시간 관련 필드들
    @Column(name = "REG_DT", length = 14)
    private String regDt; // 등록일시 (MTS 포맷)

    @Column(name = "SEND_DT", length = 14)
    private String sendDt; // 발송일시 (MTS 포맷)

    @Column(name = "RSLT_DT", length = 14)
    private String rsltDt; // 결과수신일시 (MTS 포맷)

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt;

    @Column(name = "DELIVERED_AT")
    private LocalDateTime deliveredAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}