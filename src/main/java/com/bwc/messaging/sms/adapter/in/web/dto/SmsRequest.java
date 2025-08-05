package com.bwc.messaging.sms.adapter.in.web.dto;

import com.bwc.messaging.shared.domain.MessageType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * SMS 발송 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {
    
    @NotBlank(message = "메시지 ID는 필수입니다")
    private String messageId;
    
    @NotNull(message = "메시지 타입은 필수입니다")
    private MessageType type;
    
    @NotBlank(message = "발신자는 필수입니다")
    private String sender;
    
    @NotBlank(message = "발신자 번호는 필수입니다")
    private String senderNumber;
    
    @NotBlank(message = "수신자 번호는 필수입니다")
    private String receiver;
    
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}