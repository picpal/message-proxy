package com.bwc.messaging.sns.application;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.sns.domain.SnsMessage;

/**
 * SNS 발송 채널 선택 로직
 */
@Component
public class SnsChannelRouter {
    
    public MessageChannel selectChannel(SnsMessage message) {
        // 웹훅 URL이나 채널 정보를 기반으로 플랫폼 결정
        
        if (isDiscordMessage(message)) {
            return MessageChannel.FACEBOOK_MESSENGER; // 임시로 Facebook으로 매핑
        }
        
        if (isSlackMessage(message)) {
            return MessageChannel.LINE; // 임시로 Line으로 매핑
        }
        
        if (isTeamsMessage(message)) {
            return MessageChannel.LINE; // 임시로 Line으로 매핑
        }
        
        // 메시지 타입으로 판단
        return switch (message.getType()) {
            case KAKAO_TALK, KAKAO_ALIM_TALK, KAKAO_FRIEND_TALK -> MessageChannel.KAKAO_TALK;
            case LINE -> MessageChannel.LINE;
            case FACEBOOK_MESSENGER -> MessageChannel.FACEBOOK_MESSENGER;
            default -> MessageChannel.FACEBOOK_MESSENGER; // 기본값
        };
    }
    
    private boolean isDiscordMessage(SnsMessage message) {
        return message.getWebhookUrl() != null && 
               message.getWebhookUrl().contains("discord.com/api/webhooks");
    }
    
    private boolean isSlackMessage(SnsMessage message) {
        return message.getWebhookUrl() != null && 
               message.getWebhookUrl().contains("hooks.slack.com");
    }
    
    private boolean isTeamsMessage(SnsMessage message) {
        return message.getWebhookUrl() != null && 
               message.getWebhookUrl().contains("outlook.office.com");
    }
    
    /**
     * 채널 장애 시 대체 채널 선택
     */
    public MessageChannel selectFallbackChannel(MessageChannel failedChannel) {
        return switch (failedChannel) {
            case FACEBOOK_MESSENGER -> MessageChannel.LINE;
            case LINE -> MessageChannel.KAKAO_TALK;
            case KAKAO_TALK -> MessageChannel.FACEBOOK_MESSENGER;
            default -> MessageChannel.FACEBOOK_MESSENGER;
        };
    }
}