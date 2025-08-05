package com.bwc.messaging.email.application.service;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.email.application.port.in.*;
import com.bwc.messaging.email.application.port.out.EmailRepositoryPort;
import com.bwc.messaging.email.application.port.out.EmailVendorPort;
import com.bwc.messaging.email.domain.EmailMessage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Email 관련 Use Case들을 구현하는 Application Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements SendEmailUseCase, GetEmailStatusUseCase {
    
    private final EmailRepositoryPort emailRepositoryPort;
    private final EmailVendorPort emailVendorPort;
    
    @Override
    @Transactional
    public MessageResult sendEmail(SendEmailCommand command) {
        log.info("Sending Email message: {}", command.getMessageId());
        
        try {
            // Command를 Domain 객체로 변환
            EmailMessage message = command.toMessage();
            
            // 1. 메시지 유효성 검증
            if (!message.isValid()) {
                return MessageResult.failure(message.getMessageId(), "INVALID_MESSAGE", "Invalid email format");
            }

            // 2. 메시지 저장
            emailRepositoryPort.save(message);
            
            // 3. 발송 처리 (채널 선택 + 발송은 EmailVendorPort에서 처리)
            MessageResult result = emailVendorPort.send(message);
            
            // 4. 상태 업데이트
            emailRepositoryPort.updateStatus(message.getMessageId(), result.getStatus());
            
            log.info("Email sent successfully: {}", message.getMessageId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send Email: {}", command.getMessageId(), e);
            emailRepositoryPort.updateStatus(command.getMessageId(), MessageStatus.FAILED);
            return MessageResult.failure(command.getMessageId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public MessageStatus getEmailStatus(GetEmailStatusQuery query) {
        return emailRepositoryPort.findById(query.getMessageId())
            .map(EmailMessage::getStatus)
            .orElse(MessageStatus.FAILED);
    }
}