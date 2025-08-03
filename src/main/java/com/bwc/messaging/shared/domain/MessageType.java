package com.bwc.messaging.shared.domain;

public enum MessageType {
    SMS("단문메시지"),
    MMS("멀티미디어메시지"), 
    LMS("장문메시지"),
    EMAIL("이메일"),
    KAKAO_TALK("카카오톡"),
    KAKAO_ALIM_TALK("카카오 알림톡"),
    KAKAO_FRIEND_TALK("카카오 친구톡"),
    LINE("라인"),
    FACEBOOK_MESSENGER("페이스북 메신저"),
    PUSH_NOTIFICATION("푸시알림"),
    VOICE_CALL("음성전화"),
    RCS("RCS메시지");
    
    private final String description;
    
    MessageType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isSmsType() {
        return this == SMS || this == MMS || this == LMS;
    }
    
    public boolean isSnsType() {
        return this == KAKAO_TALK || this == KAKAO_ALIM_TALK || 
               this == KAKAO_FRIEND_TALK || this == LINE || this == FACEBOOK_MESSENGER;
    }
    
    public boolean isPushType() {
        return this == PUSH_NOTIFICATION;
    }
    
    public boolean isEmailType() {
        return this == EMAIL;
    }
}