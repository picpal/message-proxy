package com.bwc.messaging.sns.application.service;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.sns.application.port.in.*;
import com.bwc.messaging.sns.application.port.out.SnsRepositoryPort;
import com.bwc.messaging.sns.application.port.out.SnsVendorPort;
import com.bwc.messaging.sns.domain.SnsMessage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SNS 관련 Use Case들을 구현하는 Application Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SnsService implements SendSnsUseCase, GetSnsStatusUseCase {
    
    private final SnsRepositoryPort snsRepositoryPort;
    private final SnsVendorPort snsVendorPort;
    
    @Override
    @Transactional
    public MessageResult sendSns(SendSnsCommand command) {
        log.info("Sending SNS message: {}", command.getMessageId());
        
        try {
            // Command를 Domain 객체로 변환
            SnsMessage message = command.toMessage();
            
            // 1. 메시지 유효성 검증
            if (!message.isValid()) {
                return MessageResult.failure(message.getMessageId(), "INVALID_MESSAGE", "Invalid SNS format");
            }

            // 2. 메시지 저장
            snsRepositoryPort.save(message);
            
            // 3. 발송 처리 (채널 선택 + 발송은 SnsVendorPort에서 처리)
            MessageResult result = snsVendorPort.send(message);
            
            // 4. 상태 업데이트
            snsRepositoryPort.updateStatus(message.getMessageId(), result.getStatus());
            
            log.info("SNS sent successfully: {}", message.getMessageId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send SNS: {}", command.getMessageId(), e);
            snsRepositoryPort.updateStatus(command.getMessageId(), MessageStatus.FAILED);
            return MessageResult.failure(command.getMessageId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public MessageStatus getSnsStatus(GetSnsStatusQuery query) {
        return snsRepositoryPort.findById(query.getMessageId())
            .map(SnsMessage::getStatus)
            .orElse(MessageStatus.FAILED);
    }
}