package com.bwc.messaging.sms.application.service;

import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.application.port.in.*;
import com.bwc.messaging.sms.application.port.out.SmsRepositoryPort;
import com.bwc.messaging.sms.application.port.out.SmsVendorPort;
import com.bwc.messaging.sms.domain.SmsMessage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SMS 관련 Use Case들을 구현하는 Application Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService implements SendSmsUseCase, GetSmsStatusUseCase, RetrySmsUseCase {
    
    private final SmsRepositoryPort smsRepositoryPort;
    private final SmsVendorPort smsVendorPort;
    
    @Override
    @Transactional
    public MessageResult sendSms(SendSmsCommand command) {
        log.info("Sending SMS message: {}", command.getMessageId());
        
        try {
            // Command를 Domain 객체로 변환
            SmsMessage message = command.toMessage();
            
            // 1. 메시지 타입 결정 (SMS/LMS)
            if (message.getContent().getBytes().length > 80 && message.getType() == MessageType.SMS) {
                message.setType(MessageType.LMS);
            }

            // 2. 메시지 유효성 검증
            if (!message.isValid()) {
                return MessageResult.failure(message.getMessageId(), "INVALID_MESSAGE", "Invalid message format");
            }

            // 3. 메시지 저장
            smsRepositoryPort.save(message);
            
            // 4. 발송 처리 (채널 선택 + 발송은 SmsVendorPort에서 처리)
            MessageResult result = smsVendorPort.send(message);
            
            // 5. 상태 업데이트
            smsRepositoryPort.updateStatus(message.getMessageId(), result.getStatus());
            
            log.info("SMS sent successfully: {}", message.getMessageId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", command.getMessageId(), e);
            smsRepositoryPort.updateStatus(command.getMessageId(), MessageStatus.FAILED);
            return MessageResult.failure(command.getMessageId(), "SEND_ERROR", e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public MessageStatus getSmsStatus(GetSmsStatusQuery query) {
        return smsRepositoryPort.findById(query.getMessageId())
            .map(SmsMessage::getStatus)
            .orElse(MessageStatus.FAILED);
    }
    
    @Override
    @Transactional
    public void retrySms(RetrySmsCommand command) {
        smsRepositoryPort.findById(command.getMessageId())
            .ifPresent(message -> {
                smsRepositoryPort.incrementRetryCount(command.getMessageId());
                SendSmsCommand sendCommand = SendSmsCommand.from(message);
                sendSms(sendCommand);
            });
    }
}