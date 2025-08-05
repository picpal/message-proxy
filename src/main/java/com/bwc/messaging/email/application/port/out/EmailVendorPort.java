package com.bwc.messaging.email.application.port.out;

import com.bwc.messaging.shared.domain.MessageChannel;
import com.bwc.messaging.shared.domain.MessageResult;
import com.bwc.messaging.email.domain.EmailMessage;

/**
 * Email 벤더 포트 인터페이스
 * 외부 Email 발송 서비스와의 통신을 담당
 */
public interface EmailVendorPort {
    
    /**
     * Email 메시지 발송
     */
    MessageResult send(EmailMessage message);
    
    /**
     * 발송 채널 선택
     */
    MessageChannel selectChannel(EmailMessage message);
    
    /**
     * 사용 가능한 채널 목록 조회
     */
    java.util.List<MessageChannel> getAvailableChannels();
    
    /**
     * 특정 채널의 헬스 체크
     */
    boolean isChannelHealthy(MessageChannel channel);
}