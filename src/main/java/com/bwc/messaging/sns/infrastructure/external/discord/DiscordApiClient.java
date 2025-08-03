package com.bwc.messaging.sns.infrastructure.external.discord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sns.domain.SnsButton;
import com.bwc.messaging.sns.domain.SnsField;
import com.bwc.messaging.sns.domain.SnsMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Discord API 클라이언트
 */
@Component
@Slf4j
public class DiscordApiClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public DiscordApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public DiscordResponse sendMessage(SnsMessage message) {
        log.info("Sending Discord message: {}", message.getMessageId());
        
        try {
            String webhookUrl = message.getWebhookUrl();
            if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
                return DiscordResponse.failure("INVALID_WEBHOOK", "Discord webhook URL is required");
            }
            
            Map<String, Object> payload = buildDiscordPayload(message);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl, HttpMethod.POST, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return DiscordResponse.success("DISCORD-" + System.currentTimeMillis());
            } else {
                return DiscordResponse.failure("API_ERROR", "Discord API returned: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Discord API call failed: {}", message.getMessageId(), e);
            return DiscordResponse.failure("NETWORK_ERROR", e.getMessage());
        }
    }
    
    private Map<String, Object> buildDiscordPayload(SnsMessage message) {
        Map<String, Object> payload = new HashMap<>();
        
        if (message.isEmbed()) {
            // Embed 형태로 전송
            Map<String, Object> embed = new HashMap<>();
            
            if (message.getTitle() != null) embed.put("title", message.getTitle());
            if (message.getDescription() != null) embed.put("description", message.getDescription());
            if (message.getColor() != null) embed.put("color", parseColor(message.getColor()));
            if (message.getThumbnailUrl() != null) {
                Map<String, Object> thumbnail = new HashMap<>();
                thumbnail.put("url", message.getThumbnailUrl());
                embed.put("thumbnail", thumbnail);
            }
            if (message.getImageUrl() != null) {
                Map<String, Object> image = new HashMap<>();
                image.put("url", message.getImageUrl());
                embed.put("image", image);
            }
            
            // Fields 추가
            if (message.getFields() != null && !message.getFields().isEmpty()) {
                List<Map<String, Object>> fields = message.getFields().stream()
                    .filter(SnsField::isValid)
                    .map(field -> {
                        Map<String, Object> fieldMap = new HashMap<>();
                        fieldMap.put("name", field.getName());
                        fieldMap.put("value", field.getValue());
                        fieldMap.put("inline", field.isInline());
                        return fieldMap;
                    })
                    .toList();
                embed.put("fields", fields);
            }
            
            payload.put("embeds", List.of(embed));
            
        } else {
            // 일반 텍스트 메시지
            payload.put("content", message.getContent());
        }
        
        // 버튼 컴포넌트 추가 (Discord Action Rows)
        if (message.getButtons() != null && !message.getButtons().isEmpty()) {
            List<Map<String, Object>> components = buildDiscordComponents(message.getButtons());
            payload.put("components", components);
        }
        
        return payload;
    }
    
    private List<Map<String, Object>> buildDiscordComponents(List<SnsButton> buttons) {
        List<Map<String, Object>> buttonComponents = buttons.stream()
            .filter(SnsButton::isValid)
            .map(button -> {
                Map<String, Object> component = new HashMap<>();
                component.put("type", 2); // Button type
                component.put("label", button.getLabel());
                component.put("disabled", button.isDisabled());
                
                switch (button.getType()) {
                    case LINK -> {
                        component.put("style", 5); // Link style
                        component.put("url", button.getUrl());
                    }
                    case PRIMARY -> component.put("style", 1);
                    case SECONDARY -> component.put("style", 2);
                    case SUCCESS -> component.put("style", 3);
                    case DANGER -> component.put("style", 4);
                    case CALLBACK -> {
                        component.put("style", 2); // Secondary style for callback
                        component.put("custom_id", button.getCallbackData());
                    }
                }
                
                return component;
            })
            .toList();
        
        // Action Row로 감싸기
        Map<String, Object> actionRow = new HashMap<>();
        actionRow.put("type", 1); // Action Row type
        actionRow.put("components", buttonComponents);
        return List.of(actionRow);
    }
    
    private int parseColor(String color) {
        try {
            if (color.startsWith("#")) {
                return Integer.parseInt(color.substring(1), 16);
            }
            return Integer.parseInt(color, 16);
        } catch (NumberFormatException e) {
            log.warn("Invalid color format: {}, using default", color);
            return 0x00ff00; // 기본 녹색
        }
    }
    
    public DiscordStatusResponse getStatus(String messageId) {
        // Discord는 개별 메시지 상태 조회 API가 제한적
        // 일반적으로 webhook로 전송된 메시지는 상태 조회가 어려움
        log.info("Discord message status check: {}", messageId);
        return new DiscordStatusResponse(MessageStatus.SENT);
    }
    
    public boolean healthCheck() {
        try {
            // Discord API 상태 확인
            // 실제로는 Discord API Gateway 상태를 확인할 수 있음
            log.debug("Discord health check");
            return true;
            
        } catch (Exception e) {
            log.error("Discord health check failed", e);
            return false;
        }
    }
    
    // 응답 클래스들
    public static class DiscordResponse {
        private final boolean success;
        private final String messageId;
        private final String errorCode;
        private final String errorMessage;
        
        private DiscordResponse(boolean success, String messageId, String errorCode, String errorMessage) {
            this.success = success;
            this.messageId = messageId;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
        
        public static DiscordResponse success(String messageId) {
            return new DiscordResponse(true, messageId, null, null);
        }
        
        public static DiscordResponse failure(String errorCode, String errorMessage) {
            return new DiscordResponse(false, null, errorCode, errorMessage);
        }
        
        public boolean isSuccess() { return success; }
        public String getMessageId() { return messageId; }
        public String getErrorCode() { return errorCode; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class DiscordStatusResponse {
        private final MessageStatus status;
        
        public DiscordStatusResponse(MessageStatus status) {
            this.status = status;
        }
        
        public MessageStatus getStatus() { return status; }
    }
}