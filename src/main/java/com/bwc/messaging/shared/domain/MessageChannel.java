package com.bwc.messaging.shared.domain;

/**
 * 메시지 발송 채널 (업체별 구분)
 */
public enum MessageChannel {
    
    // SMS 채널
    LGU_V1("LGU V1", MessageType.SMS),
    LGU_V2("LGU V2", MessageType.SMS),
    MTS("MTS", MessageType.SMS),
    
    // Email 채널  
    SMTP("SMTP", MessageType.EMAIL),
    AWS_SES("AWS SES", MessageType.EMAIL),
    SENDGRID("SendGrid", MessageType.EMAIL),
    GMAIL_API("Gmail API", MessageType.EMAIL),
    
    // SNS 채널
    KAKAO_TALK("카카오톡", MessageType.KAKAO_TALK),
    KAKAO_ALIM_TALK("카카오 알림톡", MessageType.KAKAO_ALIM_TALK),
    KAKAO_FRIEND_TALK("카카오 친구톡", MessageType.KAKAO_FRIEND_TALK),
    LINE("라인", MessageType.LINE),
    FACEBOOK_MESSENGER("페이스북 메신저", MessageType.FACEBOOK_MESSENGER),
    
    // Push 채널
    FCM("Firebase Cloud Messaging", MessageType.PUSH_NOTIFICATION),
    APNS("Apple Push Notification", MessageType.PUSH_NOTIFICATION);
    
    private final String description;
    private final MessageType supportedType;
    
    MessageChannel(String description, MessageType supportedType) {
        this.description = description;
        this.supportedType = supportedType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public MessageType getSupportedType() {
        return supportedType;
    }
    
    public boolean supports(MessageType messageType) {
        return this.supportedType == messageType;
    }
    
    // SMS 채널 여부
    public boolean isSmsChannel() {
        return supportedType.isSmsType();
    }
    
    // Email 채널 여부  
    public boolean isEmailChannel() {
        return supportedType.isEmailType();
    }
    
    // SNS 채널 여부
    public boolean isSnsChannel() {
        return supportedType.isSnsType();
    }
    
    // Push 채널 여부
    public boolean isPushChannel() {
        return supportedType.isPushType();
    }
}