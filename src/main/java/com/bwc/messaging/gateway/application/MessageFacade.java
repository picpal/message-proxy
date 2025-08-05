package com.bwc.messaging.gateway.application;

import org.springframework.stereotype.Service;

import com.bwc.messaging.gateway.presentation.dto.UnifiedMessageRequest;
import com.bwc.messaging.gateway.presentation.dto.UnifiedMessageResponse;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.shared.domain.MessageStatus;
import com.bwc.messaging.shared.domain.MessageType;
import com.bwc.messaging.sms.application.port.in.*;
import com.bwc.messaging.email.application.port.in.*;
import com.bwc.messaging.sns.application.port.in.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 통합 메시징 파사드
 * 모든 메시지 타입의 진입점
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageFacade {
    
    private final SendSmsUseCase sendSmsUseCase;
    private final GetSmsStatusUseCase getSmsStatusUseCase;
    private final SendEmailUseCase sendEmailUseCase;
    private final GetEmailStatusUseCase getEmailStatusUseCase;
    private final SendSnsUseCase sendSnsUseCase;
    private final GetSnsStatusUseCase getSnsStatusUseCase;
    // TODO: PushApplicationService 추가
    
    public UnifiedMessageResponse sendMessage(UnifiedMessageRequest request) {
        log.info("Processing unified message request: type={}, messageId={}", 
            request.getType(), request.getMessageId());
        
        try {
            MessageResult result = switch (request.getType()) {
                case SMS, LMS, MMS -> sendSmsMessage(request);
                case EMAIL -> sendEmailMessage(request);
                case KAKAO_TALK, KAKAO_ALIM_TALK, KAKAO_FRIEND_TALK, LINE, FACEBOOK_MESSENGER -> sendSnsMessage(request);
                case PUSH_NOTIFICATION -> sendPushMessage(request);
                default -> throw new IllegalArgumentException("Unsupported message type: " + request.getType());
            };
            
            return UnifiedMessageResponse.from(result);
            
        } catch (Exception e) {
            log.error("Failed to process message request: {}", request.getMessageId(), e);
            return UnifiedMessageResponse.failure(request.getMessageId(), "PROCESSING_ERROR", e.getMessage());
        }
    }
    
    public MessageStatus getMessageStatus(String messageId, MessageType messageType) {
        return switch (messageType) {
            case SMS, LMS, MMS -> getSmsStatusUseCase.getSmsStatus(GetSmsStatusQuery.builder().messageId(messageId).build());
            case EMAIL -> getEmailStatusUseCase.getEmailStatus(GetEmailStatusQuery.builder().messageId(messageId).build());
            case KAKAO_TALK, KAKAO_ALIM_TALK, KAKAO_FRIEND_TALK, LINE, FACEBOOK_MESSENGER -> getSnsStatusUseCase.getSnsStatus(GetSnsStatusQuery.builder().messageId(messageId).build());
            case PUSH_NOTIFICATION -> getPushStatus(messageId);
            default -> MessageStatus.FAILED;
        };
    }
    
    private MessageResult sendSmsMessage(UnifiedMessageRequest request) {
        var smsMessage = request.toSmsMessage();
        SendSmsCommand command = SendSmsCommand.from(smsMessage);
        return sendSmsUseCase.sendSms(command);
    }
    
    private MessageResult sendEmailMessage(UnifiedMessageRequest request) {
        var emailMessage = request.toEmailMessage();
        SendEmailCommand command = SendEmailCommand.from(emailMessage);
        return sendEmailUseCase.sendEmail(command);
    }
    
    private MessageResult sendSnsMessage(UnifiedMessageRequest request) {
        var snsMessage = request.toSnsMessage();
        SendSnsCommand command = SendSnsCommand.from(snsMessage);
        return sendSnsUseCase.sendSns(command);
    }
    
    private MessageResult sendPushMessage(UnifiedMessageRequest request) {
        // TODO: Push 서비스 구현 후 추가
        log.warn("Push service not implemented yet");
        return MessageResult.failure(request.getMessageId(), "NOT_IMPLEMENTED", "Push service not implemented");
    }
    
    
    private MessageStatus getPushStatus(String messageId) {
        // TODO: Push 상태 조회 구현
        return MessageStatus.FAILED;
    }
}