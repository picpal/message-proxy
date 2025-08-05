package com.bwc.messaging.email.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.email.application.port.out.EmailRepositoryPort;
import com.bwc.messaging.email.domain.EmailMessage;
import com.bwc.messaging.email.domain.EmailRepository;

import lombok.RequiredArgsConstructor;

/**
 * Email Repository Port 구현체
 * 기존 Domain Repository를 래핑하여 Port 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
public class EmailRepositoryAdapter implements EmailRepositoryPort {
    
    private final EmailRepository emailRepository;
    
    @Override
    public void save(EmailMessage message) {
        emailRepository.save(message);
    }
    
    @Override
    public Optional<EmailMessage> findById(String messageId) {
        return emailRepository.findById(messageId);
    }
    
    @Override
    public void updateStatus(String messageId, MessageStatus status) {
        emailRepository.updateStatus(messageId, status);
    }
    
    @Override
    public void healthCheck() {
        // Email repository does not have healthCheck method
        // Implement basic health check by verifying connection
    }
}