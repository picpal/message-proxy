package com.bwc.messaging.sns.infrastructure.persistence;

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

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;

@Entity
@Table(name = "TB_SNS_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnsMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MESSAGE_ID", unique = true, nullable = false, length = 50)
    private String messageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "MESSAGE_TYPE", nullable = false)
    private MessageType type;

    @Column(name = "SENDER", length = 100)
    private String sender;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private MessageStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "CHANNEL")
    private MessageChannel channel;

    // SNS 특화 필드들
    @Column(name = "WEBHOOK_URL", length = 500)
    private String webhookUrl;

    @Column(name = "IS_EMBED", nullable = false)
    @Builder.Default
    private Boolean isEmbed = false;

    @Column(name = "TITLE", length = 200)
    private String title;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "COLOR", length = 7)
    private String color;

    @Column(name = "THUMBNAIL_URL", length = 500)
    private String thumbnailUrl;

    @Column(name = "IMAGE_URL", length = 500)
    private String imageUrl;

    @Column(name = "FIELDS_JSON", columnDefinition = "TEXT")
    private String fieldsJson; // JSON 형태로 저장

    @Column(name = "BUTTONS_JSON", columnDefinition = "TEXT")
    private String buttonsJson; // JSON 형태로 저장

    @Column(name = "VENDOR_MESSAGE_ID", length = 100)
    private String vendorMessageId;

    @Column(name = "RETRY_COUNT", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "ERROR_CODE", length = 50)
    private String errorCode;

    @Column(name = "ERROR_MESSAGE", length = 500)
    private String errorMessage;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}