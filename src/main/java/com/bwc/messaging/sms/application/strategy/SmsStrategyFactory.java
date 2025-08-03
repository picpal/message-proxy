package com.bwc.messaging.sms.application.strategy;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;

@Component
public class SmsStrategyFactory {
    
    private final Map<MessageChannel, SmsStrategy> strategies;
    
    public SmsStrategyFactory(java.util.List<SmsStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                SmsStrategy::getSupportedChannel,
                Function.identity()
            ));
    }
    
    public SmsStrategy getStrategy(MessageChannel channel) {
        SmsStrategy strategy = strategies.get(channel);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for channel: " + channel);
        }
        return strategy;
    }
    
    /**
     * 사용 가능한 전략 목록 조회
     */
    public java.util.List<MessageChannel> getAvailableChannels() {
        return strategies.entrySet().stream()
            .filter(entry -> entry.getValue().isHealthy())
            .map(Map.Entry::getKey)
            .toList();
    }
}