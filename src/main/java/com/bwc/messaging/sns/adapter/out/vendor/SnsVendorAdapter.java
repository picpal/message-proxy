package com.bwc.messaging.sns.adapter.out.vendor;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.sns.application.SnsChannelRouter;
import com.bwc.messaging.sns.application.port.out.SnsVendorPort;
import com.bwc.messaging.sns.application.strategy.SnsStrategy;
import com.bwc.messaging.sns.application.strategy.SnsStrategyFactory;
import com.bwc.messaging.sns.domain.SnsMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SNS Vendor Port 구현체
 * 기존 Strategy Pattern을 래핑하여 Port 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SnsVendorAdapter implements SnsVendorPort {
    
    private final SnsStrategyFactory strategyFactory;
    private final SnsChannelRouter channelRouter;
    
    @Override
    public MessageResult send(SnsMessage message) {
        try {
            // 1. 발송 채널 결정
            MessageChannel channel = selectChannel(message);
            
            // 2. 전략 패턴으로 발송
            SnsStrategy strategy = strategyFactory.getStrategy(channel);
            MessageResult result = strategy.send(message);
            
            log.info("SNS sent via channel: {} for message: {}", channel, message.getMessageId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send SNS via vendor: {}", message.getMessageId(), e);
            return MessageResult.failure(message.getMessageId(), "VENDOR_ERROR", e.getMessage());
        }
    }
    
    @Override
    public MessageChannel selectChannel(SnsMessage message) {
        return channelRouter.selectChannel(message);
    }
    
    @Override
    public java.util.List<MessageChannel> getAvailableChannels() {
        return strategyFactory.getAvailableChannels();
    }
    
    @Override
    public boolean isChannelHealthy(MessageChannel channel) {
        try {
            SnsStrategy strategy = strategyFactory.getStrategy(channel);
            return strategy.isHealthy();
        } catch (Exception e) {
            log.warn("Health check failed for channel: {}", channel, e);
            return false;
        }
    }
}