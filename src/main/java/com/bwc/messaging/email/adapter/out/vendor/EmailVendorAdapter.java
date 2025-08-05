package com.bwc.messaging.email.adapter.out.vendor;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.email.application.EmailChannelRouter;
import com.bwc.messaging.email.application.port.out.EmailVendorPort;
import com.bwc.messaging.email.application.strategy.EmailStrategy;
import com.bwc.messaging.email.application.strategy.EmailStrategyFactory;
import com.bwc.messaging.email.domain.EmailMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Email Vendor Port 구현체
 * 기존 Strategy Pattern을 래핑하여 Port 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVendorAdapter implements EmailVendorPort {
    
    private final EmailStrategyFactory strategyFactory;
    private final EmailChannelRouter channelRouter;
    
    @Override
    public MessageResult send(EmailMessage message) {
        try {
            // 1. 발송 채널 결정
            MessageChannel channel = selectChannel(message);
            
            // 2. 전략 패턴으로 발송
            EmailStrategy strategy = strategyFactory.getStrategy(channel);
            MessageResult result = strategy.send(message);
            
            log.info("Email sent via channel: {} for message: {}", channel, message.getMessageId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send Email via vendor: {}", message.getMessageId(), e);
            return MessageResult.failure(message.getMessageId(), "VENDOR_ERROR", e.getMessage());
        }
    }
    
    @Override
    public MessageChannel selectChannel(EmailMessage message) {
        return channelRouter.selectChannel(message);
    }
    
    @Override
    public java.util.List<MessageChannel> getAvailableChannels() {
        return strategyFactory.getAvailableChannels();
    }
    
    @Override
    public boolean isChannelHealthy(MessageChannel channel) {
        try {
            EmailStrategy strategy = strategyFactory.getStrategy(channel);
            return strategy.isHealthy();
        } catch (Exception e) {
            log.warn("Health check failed for channel: {}", channel, e);
            return false;
        }
    }
}