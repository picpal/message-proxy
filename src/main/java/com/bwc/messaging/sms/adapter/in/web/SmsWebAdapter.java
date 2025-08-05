package com.bwc.messaging.sms.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sms.adapter.in.web.dto.SmsRequest;
import com.bwc.messaging.sms.adapter.in.web.dto.SmsResponse;
import com.bwc.messaging.sms.application.port.in.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SMS Web Adapter (Inbound Port 구현)
 * HTTP 요청을 Use Case 호출로 변환
 */
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SMS API", description = "SMS 전용 API")
public class SmsWebAdapter {
    
    private final SendSmsUseCase sendSmsUseCase;
    private final GetSmsStatusUseCase getSmsStatusUseCase;
    private final RetrySmsUseCase retrySmsUseCase;
    
    @PostMapping("/send")
    @Operation(summary = "SMS 발송", description = "SMS 메시지를 발송합니다.")
    public ResponseEntity<SmsResponse> sendSms(@Valid @RequestBody SmsRequest request) {
        log.info("Received SMS send request: messageId={}", request.getMessageId());
        
        try {
            // Request DTO → Command 변환
            SendSmsCommand command = SendSmsCommand.builder()
                .messageId(request.getMessageId())
                .type(request.getType())
                .sender(request.getSender())
                .senderNumber(request.getSenderNumber())
                .receiver(request.getReceiver())
                .content(request.getContent())
                .build();
            
            // Use Case 실행
            MessageResult result = sendSmsUseCase.sendSms(command);
            
            // Result → Response DTO 변환
            SmsResponse response = SmsResponse.from(result);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Unexpected error in SMS send", e);
            SmsResponse errorResponse = SmsResponse.failure(
                request.getMessageId(), "INTERNAL_ERROR", "Internal server error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/{messageId}/status")
    @Operation(summary = "SMS 상태 조회", description = "발송된 SMS의 상태를 조회합니다.")
    public ResponseEntity<SmsStatusResponse> getSmsStatus(@PathVariable String messageId) {
        log.info("Getting SMS status: messageId={}", messageId);
        
        try {
            GetSmsStatusQuery query = GetSmsStatusQuery.builder()
                .messageId(messageId)
                .build();
            
            MessageStatus status = getSmsStatusUseCase.getSmsStatus(query);
            
            SmsStatusResponse response = SmsStatusResponse.builder()
                .messageId(messageId)
                .status(status)
                .build();
                
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting SMS status: {}", messageId, e);
            SmsStatusResponse errorResponse = SmsStatusResponse.builder()
                .messageId(messageId)
                .status(MessageStatus.FAILED)
                .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @PostMapping("/{messageId}/retry")
    @Operation(summary = "SMS 재전송", description = "실패한 SMS를 재전송합니다.")
    public ResponseEntity<String> retrySms(@PathVariable String messageId) {
        log.info("Retrying SMS: messageId={}", messageId);
        
        try {
            RetrySmsCommand command = RetrySmsCommand.builder()
                .messageId(messageId)
                .build();
            
            retrySmsUseCase.retrySms(command);
            
            return ResponseEntity.ok("SMS retry initiated");
            
        } catch (Exception e) {
            log.error("Error retrying SMS: {}", messageId, e);
            return ResponseEntity.internalServerError().body("Retry failed");
        }
    }
    
    // 응답용 DTO
    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SmsStatusResponse {
        private String messageId;
        private MessageStatus status;
    }
}