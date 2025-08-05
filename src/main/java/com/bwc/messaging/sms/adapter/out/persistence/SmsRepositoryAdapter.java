package com.bwc.messaging.sms.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sms.application.port.out.SmsRepositoryPort;
import com.bwc.messaging.sms.domain.SmsMessage;
import com.bwc.messaging.sms.domain.SmsRepository;

import lombok.RequiredArgsConstructor;

/**
 * SMS Repository Port 구현체
 * 기존 Domain Repository를 래핑하여 Port 인터페이스를 구현
 */
@Component
@RequiredArgsConstructor
public class SmsRepositoryAdapter implements SmsRepositoryPort {
    
    private final SmsRepository smsRepository;
    
    @Override
    public void save(SmsMessage message) {
        smsRepository.save(message);
    }
    
    @Override
    public Optional<SmsMessage> findById(String messageId) {
        return smsRepository.findById(messageId);
    }
    
    @Override
    public void updateStatus(String messageId, MessageStatus status) {
        smsRepository.updateStatus(messageId, status);
    }
    
    @Override
    public void incrementRetryCount(String messageId) {
        smsRepository.incrementRetryCount(messageId);
    }
    
    @Override
    public java.util.List<SmsMessage> findPendingMessages(int limit) {
        return smsRepository.findPendingMessages(limit);
    }
    
    @Override
    public void healthCheck() {
        smsRepository.healthCheck();
    }
}