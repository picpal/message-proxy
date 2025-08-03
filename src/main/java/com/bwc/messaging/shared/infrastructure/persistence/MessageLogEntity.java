package com.bwc.messaging.shared.infrastructure.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_MG_MESSAGE_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SESS_GUID", length = 50)
    private String sessGuid;

    @Column(name = "SEND_UID", length = 50)
    private String sendUid;

    @Column(name = "SERVICE_CODE", length = 40)
    private String serviceCode;

    @Column(name = "SEND_DATE", length = 8)
    private String sendDate;

    @Column(name = "SEND_TIME", length = 6)
    private String sendTime;

    @Column(name = "SEND_TYPE", length = 10)
    private String sendType;

    @Column(name = "STATUS", length = 2)
    private String status;

    @Column(name = "RECEIVER_PHONE", length = 15)
    private String receiverPhone;

    @Column(name = "SENDER_NUMBER", length = 15)
    private String senderNumber;

    @Column(name = "VENDER", length = 20)
    private String vender;

    @Column(name = "TEMPLATE_CODE", length = 40)
    private String templateCode;

    @Column(name = "RESEND_COUNT")
    private Integer resendCount;

    @Column(name = "FST_REG_DTTM")
    private LocalDateTime fstRegDttm;

    @Column(name = "FST_RGNT_NO", length = 40)
    private String fstRgntNo;

    @Column(name = "LAST_CHNG_DTTM")
    private LocalDateTime lastChngDttm;

    @Column(name = "LAST_CHGR_NO", length = 40)
    private String lastChgrNo;
}