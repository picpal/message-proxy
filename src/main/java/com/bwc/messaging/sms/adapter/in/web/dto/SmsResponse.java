package com.bwc.messaging.sms.adapter.in.web.dto;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * SMS 발송 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponse {
    
    private String messageId;
    private MessageStatus status;
    private boolean success;
    private String errorCode;
    private String errorMessage;
    
    /**
     * MessageResult에서 SmsResponse 생성
     */
    public static SmsResponse from(MessageResult result) {
        return SmsResponse.builder()
            .messageId(result.getMessageId())
            .status(result.getStatus())
            .success(result.isSuccess())
            .errorCode(result.getErrorCode())
            .errorMessage(result.getErrorMessage())
            .build();
    }
    
    /**
     * 실패 응답 생성
     */
    public static SmsResponse failure(String messageId, String errorCode, String errorMessage) {
        return SmsResponse.builder()
            .messageId(messageId)
            .status(MessageStatus.FAILED)
            .success(false)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .build();
    }
}