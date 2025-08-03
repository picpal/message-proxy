package com.bwc.messaging.gateway.presentation.dto;

import java.time.LocalDateTime;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 통합 메시지 발송 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedMessageResponse {
    
    private String messageId;
    private MessageStatus status;
    private String resultCode;
    private String resultMessage;
    private String vendorMessageId;
    private LocalDateTime processedAt;
    private boolean success;
    
    public static UnifiedMessageResponse from(MessageResult result) {
        return UnifiedMessageResponse.builder()
            .messageId(result.getMessageId())
            .status(result.getStatus())
            .resultCode(result.getResultCode())
            .resultMessage(result.getResultMessage())
            .vendorMessageId(result.getVendorMessageId())
            .processedAt(result.getProcessedAt())
            .success(result.isSuccess())
            .build();
    }
    
    public static UnifiedMessageResponse success(String messageId, String vendorMessageId) {
        return UnifiedMessageResponse.builder()
            .messageId(messageId)
            .status(MessageStatus.SENT)
            .resultCode("0000")
            .resultMessage("Success")
            .vendorMessageId(vendorMessageId)
            .processedAt(LocalDateTime.now())
            .success(true)
            .build();
    }
    
    public static UnifiedMessageResponse failure(String messageId, String errorCode, String errorMessage) {
        return UnifiedMessageResponse.builder()
            .messageId(messageId)
            .status(MessageStatus.FAILED)
            .resultCode(errorCode)
            .resultMessage(errorMessage)
            .processedAt(LocalDateTime.now())
            .success(false)
            .build();
    }
}