package com.bwc.messaging.sns.application.strategy;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sns.domain.SnsMessage;
import com.bwc.messaging.sns.infrastructure.external.discord.DiscordApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordSnsStrategy implements SnsStrategy {
    
    private final DiscordApiClient apiClient;
    
    @Override
    public MessageResult send(SnsMessage message) {
        log.info("Sending SNS message via Discord: {}", message.getMessageId());
        
        try {
            var response = apiClient.sendMessage(message);
            
            if (response.isSuccess()) {
                return MessageResult.success(message.getMessageId(), response.getMessageId());
            } else {
                return MessageResult.failure(message.getMessageId(), 
                    response.getErrorCode(), response.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("Discord SNS send failed: {}", message.getMessageId(), e);
            return MessageResult.failure(message.getMessageId(), "API_ERROR", e.getMessage());
        }
    }
    
    @Override
    public MessageStatus getStatus(String messageId) {
        try {
            var response = apiClient.getStatus(messageId);
            return response.getStatus();
        } catch (Exception e) {
            log.error("Failed to get status from Discord: {}", messageId, e);
            return MessageStatus.FAILED;
        }
    }
    
    @Override
    public boolean canSend(SnsMessage message) {
        return message.isValid() && 
               isHealthy() && 
               isDiscordMessage(message) &&
               (message.getWebhookUrl() != null || message.getChannelId() != null);
    }
    
    @Override
    public Class<SnsMessage> getSupportedMessageType() {
        return SnsMessage.class;
    }
    
    @Override
    public MessageChannel getSupportedChannel() {
        // 임시로 Facebook Messenger로 매핑 (실제로는 DISCORD 채널이 필요)
        return MessageChannel.FACEBOOK_MESSENGER; 
    }
    
    @Override
    public boolean isHealthy() {
        try {
            return apiClient.healthCheck();
        } catch (Exception e) {
            log.warn("Discord health check failed", e);
            return false;
        }
    }
    
    private boolean isDiscordMessage(SnsMessage message) {
        // Discord 메시지 여부 판단 로직
        // 실제로는 메시지 타입이나 설정으로 구분
        return message.getWebhookUrl() != null && 
               message.getWebhookUrl().contains("discord.com/api/webhooks");
    }
}