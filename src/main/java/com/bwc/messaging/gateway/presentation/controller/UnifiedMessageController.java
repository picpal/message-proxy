package com.bwc.messaging.gateway.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bwc.messaging.gateway.application.MessageFacade;
import com.bwc.messaging.gateway.presentation.dto.UnifiedMessageRequest;
import com.bwc.messaging.gateway.presentation.dto.UnifiedMessageResponse;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * 통합 메시징 API 컨트롤러
 * 모든 메시지 타입(SMS, Email, SNS, Push)을 하나의 API로 처리
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Unified Messaging API", description = "통합 메시징 API")
public class UnifiedMessageController {
    
    private final MessageFacade messageFacade;
    
    @PostMapping("/send")
    @Operation(summary = "통합 메시지 발송", description = "SMS, Email, SNS, Push 등 모든 타입의 메시지를 발송합니다.")
    public ResponseEntity<UnifiedMessageResponse> sendMessage(
            @Valid @RequestBody UnifiedMessageRequest request) {
        
        String traceId = MDC.get("traceId");
        log.info("Received message send request: type={}, messageId={}, traceId={}", 
            request.getType(), request.getMessageId(), traceId);
        
        try {
            UnifiedMessageResponse response = messageFacade.sendMessage(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Unexpected error in message send", e);
            UnifiedMessageResponse errorResponse = UnifiedMessageResponse.failure(
                request.getMessageId(), "INTERNAL_ERROR", "Internal server error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/{messageId}/status")
    @Operation(summary = "메시지 상태 조회", description = "발송된 메시지의 상태를 조회합니다.")
    public ResponseEntity<MessageStatusResponse> getMessageStatus(
            @PathVariable String messageId,
            @RequestParam MessageType messageType) {
        
        log.info("Getting message status: messageId={}, type={}", messageId, messageType);
        
        try {
            MessageStatus status = messageFacade.getMessageStatus(messageId, messageType);
            
            MessageStatusResponse response = MessageStatusResponse.builder()
                .messageId(messageId)
                .messageType(messageType)
                .status(status)
                .build();
                
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting message status: {}", messageId, e);
            MessageStatusResponse errorResponse = MessageStatusResponse.builder()
                .messageId(messageId)
                .messageType(messageType)
                .status(MessageStatus.FAILED)
                .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "서비스 상태 확인", description = "메시징 서비스의 상태를 확인합니다.")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Messaging service is healthy");
    }
    
    // 응답용 DTO
    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MessageStatusResponse {
        private String messageId;
        private MessageType messageType;
        private MessageStatus status;
    }
}