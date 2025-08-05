package com.bwc.messaging.sms.application.port.out;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.sms.domain.SmsMessage;

/**
 * SMS 벤더 포트 인터페이스
 * 외부 SMS 발송 서비스와의 통신을 담당
 */
public interface SmsVendorPort {
    
    /**
     * SMS 메시지 발송
     */
    MessageResult send(SmsMessage message);
    
    /**
     * 발송 채널 선택
     */
    MessageChannel selectChannel(SmsMessage message);
    
    /**
     * 사용 가능한 채널 목록 조회
     */
    java.util.List<MessageChannel> getAvailableChannels();
    
    /**
     * 특정 채널의 헬스 체크
     */
    boolean isChannelHealthy(MessageChannel channel);
}