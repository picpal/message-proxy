package com.bwc.messaging.sns.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sns.application.port.out.SnsRepositoryPort;
import com.bwc.messaging.sns.domain.SnsMessage;
import com.bwc.messaging.sns.domain.SnsRepository;

import lombok.RequiredArgsConstructor;

/**
 * SNS Repository Port 구현체
 * 기존 Domain Repository를 래핑하여 Port 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
public class SnsRepositoryAdapter implements SnsRepositoryPort {
    
    private final SnsRepository snsRepository;
    
    @Override
    public void save(SnsMessage message) {
        snsRepository.save(message);
    }
    
    @Override
    public Optional<SnsMessage> findById(String messageId) {
        return snsRepository.findById(messageId);
    }
    
    @Override
    public void updateStatus(String messageId, MessageStatus status) {
        snsRepository.updateStatus(messageId, status);
    }
    
    @Override
    public void healthCheck() {
        snsRepository.healthCheck();
    }
}