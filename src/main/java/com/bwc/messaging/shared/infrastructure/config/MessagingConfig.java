package com.bwc.messaging.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

/**
 * 메시징 시스템 통합 설정 - 기본 데이터소스 사용
 */
@Configuration
// @EnableJpaRepositories(basePackages = {
//     "com.bwc.messaging.shared.infrastructure.persistence",
//     "com.bwc.messaging.sms.infrastructure.persistence",
//     "com.bwc.messaging.email.infrastructure.persistence",
//     "com.bwc.messaging.sns.infrastructure.persistence"
// })
@ComponentScan(basePackages = {
    "com.bwc.messaging"
})
public class MessagingConfig {
    
    // 메시징 시스템 관련 Bean 설정들이 여기에 추가됩니다.
}