package com.bwc.messaging.sms.adapter.out.vendor;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.sms.application.SmsChannelRouter;
import com.bwc.messaging.sms.application.port.out.SmsVendorPort;
import com.bwc.messaging.sms.application.strategy.SmsStrategy;
import com.bwc.messaging.sms.application.strategy.SmsStrategyFactory;
import com.bwc.messaging.sms.domain.SmsMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SMS Vendor Port 구현체
 * 기존 Strategy Pattern을 래핑하여 Port 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsVendorAdapter implements SmsVendorPort {
    
    private final SmsStrategyFactory strategyFactory;
    private final SmsChannelRouter channelRouter;
    
    @Override
    public MessageResult send(SmsMessage message) {
        try {
            // 1. 발송 채널 결정
            MessageChannel channel = selectChannel(message);
            
            // 2. 전략 패턴으로 발송
            SmsStrategy strategy = strategyFactory.getStrategy(channel);
            MessageResult result = strategy.send(message);
            
            log.info("SMS sent via channel: {} for message: {}", channel, message.getMessageId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send SMS via vendor: {}", message.getMessageId(), e);
            return MessageResult.failure(message.getMessageId(), "VENDOR_ERROR", e.getMessage());
        }
    }
    
    @Override
    public MessageChannel selectChannel(SmsMessage message) {
        return channelRouter.selectChannel(message);
    }
    
    @Override
    public java.util.List<MessageChannel> getAvailableChannels() {
        return strategyFactory.getAvailableChannels();
    }
    
    @Override
    public boolean isChannelHealthy(MessageChannel channel) {
        try {
            SmsStrategy strategy = strategyFactory.getStrategy(channel);
            return strategy.isHealthy();
        } catch (Exception e) {
            log.warn("Health check failed for channel: {}", channel, e);
            return false;
        }
    }
}