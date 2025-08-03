package com.bwc.messaging.email.application.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;

@Component
public class EmailStrategyFactory {
    
    private final Map<MessageChannel, EmailStrategy> strategies;
    
    public EmailStrategyFactory(List<EmailStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                EmailStrategy::getSupportedChannel,
                Function.identity()
            ));
    }
    
    public EmailStrategy getStrategy(MessageChannel channel) {
        EmailStrategy strategy = strategies.get(channel);
        if (strategy == null) {
            throw new IllegalArgumentException("No Email strategy found for channel: " + channel);
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