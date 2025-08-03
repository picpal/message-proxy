package com.bwc.messaging.sns.application.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;

@Component
public class SnsStrategyFactory {
    
    private final Map<MessageChannel, SnsStrategy> strategies;
    
    public SnsStrategyFactory(List<SnsStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                SnsStrategy::getSupportedChannel,
                Function.identity()
            ));
    }
    
    public SnsStrategy getStrategy(MessageChannel channel) {
        SnsStrategy strategy = strategies.get(channel);
        if (strategy == null) {
            throw new IllegalArgumentException("No SNS strategy found for channel: " + channel);
        }
        return strategy;
    }
    
    /**
     * 사용 가능한 전략 목록 조회
     */
    public List<MessageChannel> getAvailableChannels() {
        return strategies.entrySet().stream()
            .filter(entry -> entry.getValue().isHealthy())
            .map(Map.Entry::getKey)
            .toList();
    }
}