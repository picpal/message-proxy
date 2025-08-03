package com.bwc.messaging.sms.application;

import org.springframework.stereotype.Component;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.sms.domain.SmsMessage;

/**
 * SMS 발송 채널 선택 로직
 */
@Component
public class SmsChannelRouter {
    
    public MessageChannel selectChannel(SmsMessage message) {
        // 현재는 간단한 라운드로빈, 추후 로드밸런싱/가중치 적용 가능
        
        // 서비스 코드나 발송량에 따른 채널 선택 로직
        String serviceCode = message.getSender();
        
        if (serviceCode != null && serviceCode.startsWith("LGU")) {
            return selectLguChannel(message);
        }
        
        if (serviceCode != null && serviceCode.startsWith("MTS")) {
            return MessageChannel.MTS;
        }
        
        // 기본값
        return MessageChannel.LGU_V1;
    }
    
    private MessageChannel selectLguChannel(SmsMessage message) {
        // LGU V1 vs V2 선택 로직
        // 예: 메시지 타입, 발송량, 시간대 등을 고려
        
        if (message.getContent().getBytes().length > 80) {
            return MessageChannel.LGU_V2; // 긴 메시지는 V2로
        }
        
        return MessageChannel.LGU_V1; // 기본값
    }
    
    /**
     * 채널 장애 시 대체 채널 선택
     */
    public MessageChannel selectFallbackChannel(MessageChannel failedChannel) {
        return switch (failedChannel) {
            case LGU_V1 -> MessageChannel.LGU_V2;
            case LGU_V2 -> MessageChannel.MTS;
            case MTS -> MessageChannel.LGU_V1;
            default -> MessageChannel.LGU_V1;
        };
    }
}