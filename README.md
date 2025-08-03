# Message Proxy

통합 메시징 시스템 - SMS, SNS, Email, Push 알림을 통합 관리하는 멀티벤더 메시징 플랫폼

## 개발 환경

### 요구사항
- Java 17+
- Spring Boot 3.2.2
- H2 Database (local)
- Oracle (test, prod)
- Gradle

### 로컬 실행
```bash
# 애플리케이션 실행
./gradlew bootRun

# 프로필 지정 실행  
./gradlew bootRun --args='--spring.profiles.active=local'
```

### H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:ma-db`
- Username: `sa`
- Password: (empty)

## API 엔드포인트

### 통합 메시지 발송
```
POST /api/messages/send
```

### 메시지 상태 조회
```
GET /api/messages/{messageId}/status?type={messageType}
```

## 지원 메시지 타입

### SMS (단문/장문 메시지)
- **LGU V1**: 레거시 API 방식, 예약발송 지원, 광고/일반 구분
- **LGU V2**: REST API 방식, OAuth 인증, JSON 확장 필드 지원
- **MTS**: 통신사별 코드 관리, 업체별 과금 처리

### SNS (소셜 네트워크 서비스)
- **Discord**: 웹훅 기반 메시지 발송, Embed 지원

### Email
- **SMTP**: 표준 SMTP 프로토콜 지원

## 아키텍처

### Domain-Driven Hexagonal Architecture

```
com.bwc.messaging/
├── shared/                    # 공통 도메인 및 인프라
│   ├── domain/               # 공통 도메인 모델
│   │   ├── Message.java      # 기본 메시지 추상 클래스
│   │   ├── MessageType.java  # 메시지 타입 열거형
│   │   ├── MessageChannel.java # 발송 채널 열거형
│   │   ├── MessageStatus.java  # 메시지 상태 열거형
│   │   └── MessageResult.java  # 발송 결과 모델
│   └── infrastructure/       # 공통 인프라
│       ├── config/          # 설정 클래스
│       └── persistence/     # 공통 엔티티 및 리포지토리
│
├── sms/                      # SMS 도메인
│   ├── domain/              # SMS 도메인 모델
│   │   ├── SmsMessage.java  # SMS 메시지 도메인 객체
│   │   └── SmsRepository.java # SMS 리포지토리 인터페이스
│   ├── application/         # SMS 애플리케이션 서비스
│   │   ├── SmsApplicationService.java # 메인 비즈니스 로직
│   │   ├── SmsChannelRouter.java      # 발송사별 채널 라우팅
│   │   └── strategy/        # 전략 패턴 구현
│   │       ├── SmsStrategy.java           # 전략 인터페이스
│   │       ├── SmsStrategyFactory.java    # 전략 팩토리
│   │       └── impl/        # 발송사별 전략 구현
│   │           ├── LguV1SmsStrategy.java  # LGU V1 전략
│   │           ├── LguV2SmsStrategy.java  # LGU V2 전략
│   │           └── MtsSmsStrategy.java    # MTS 전략
│   └── infrastructure/      # SMS 인프라
│       └── persistence/     # 발송사별 전용 테이블 및 JPA 구현
│           ├── lgu/         # LGU 관련
│           │   ├── v1/      # LGU V1 전용
│           │   │   ├── LguV1MessageEntity.java      # LGU V1 엔티티
│           │   │   ├── LguV1MessageJpaRepository.java # LGU V1 JPA
│           │   │   └── LguV1SmsRepositoryImpl.java   # LGU V1 구현체
│           │   └── v2/      # LGU V2 전용
│           │       ├── LguV2MessageEntity.java      # LGU V2 엔티티
│           │       ├── LguV2MessageJpaRepository.java # LGU V2 JPA
│           │       └── LguV2SmsRepositoryImpl.java   # LGU V2 구현체
│           └── mts/         # MTS 전용
│               ├── MtsMessageEntity.java        # MTS 엔티티
│               ├── MtsMessageJpaRepository.java # MTS JPA
│               └── MtsSmsRepositoryImpl.java    # MTS 구현체
│
├── sns/                     # SNS 도메인 (Discord, Kakao, Line 등)
│   ├── domain/             # SNS 도메인 모델
│   │   ├── SnsMessage.java # SNS 메시지 엔티티
│   │   ├── SnsField.java   # SNS 필드 (Embed용)
│   │   ├── SnsButton.java  # SNS 버튼
│   │   └── SnsRepository.java # SNS 리포지토리 인터페이스
│   ├── application/        # SNS 애플리케이션 서비스
│   │   ├── SnsApplicationService.java # 메인 서비스
│   │   ├── SnsChannelRouter.java      # 채널 라우팅
│   │   └── strategy/       # 전략 패턴 구현
│   │       ├── SnsStrategy.java       # 전략 인터페이스
│   │       ├── SnsStrategyFactory.java # 전략 팩토리
│   │       └── DiscordSnsStrategy.java # Discord 구현
│   └── infrastructure/     # SNS 인프라
│       ├── external/       # 외부 API 클라이언트
│       │   ├── discord/    # Discord API 클라이언트
│       │   ├── slack/      # Slack API (예정)
│       │   └── teams/      # Teams API (예정)
│       └── persistence/    # JPA 리포지토리 구현
│
├── email/                  # Email 도메인
│   ├── domain/            # Email 도메인 모델
│   │   ├── EmailMessage.java    # Email 메시지 엔티티
│   │   ├── EmailAttachment.java # 첨부파일 모델
│   │   └── EmailRepository.java # Email 리포지토리 인터페이스
│   ├── application/       # Email 애플리케이션 서비스
│   │   ├── EmailApplicationService.java # 메인 서비스
│   │   ├── EmailChannelRouter.java      # 채널 라우팅
│   │   └── strategy/      # 전략 패턴 구현
│   │       ├── EmailStrategy.java       # 전략 인터페이스
│   │       ├── EmailStrategyFactory.java # 전략 팩토리
│   │       └── impl/      # SMTP, Gmail API 등 구현
│   └── infrastructure/    # Email 인프라
│       ├── external/      # SMTP, Gmail API 클라이언트
│       └── persistence/   # JPA 리포지토리 구현
│
└── gateway/               # 통합 게이트웨이 (Facade 패턴)
    ├── application/       # 게이트웨이 서비스
    │   └── MessageFacade.java # 통합 메시지 파사드
    └── presentation/      # REST API 계층
        ├── controller/    # REST 컨트롤러
        └── dto/          # 요청/응답 DTO
```

### 핵심 설계 원칙

1. **Hexagonal Architecture**: 각 도메인은 Port(인터페이스)와 Adapter(구현체)로 분리
2. **Strategy Pattern**: 각 메시지 타입별로 다양한 발송 업체 지원
3. **Facade Pattern**: 단일 진입점을 통한 통합 API 제공
4. **Domain-Driven Design**: 도메인별 경계 명확히 구분
5. **Multi-Vendor Support**: 발송사별 전용 테이블과 최적화된 데이터 구조

### SMS 발송사별 테이블 구조

#### 1. LGU V1 테이블 (`TB_LGU_V1_SMS_MESSAGE`)
```sql
-- LGU V1 전용 필드들
CALLBACK_NUMBER     VARCHAR(20)   -- 발신번호
SERVICE_TYPE        VARCHAR(10)   -- SMS/LMS/MMS 
TEMPLATE_ID         VARCHAR(30)   -- 템플릿 ID
AD_FLAG             VARCHAR(1)    -- 광고 여부 (Y/N)
RESERVE_DATE        VARCHAR(8)    -- 예약발송일 (YYYYMMDD)
RESERVE_TIME        VARCHAR(6)    -- 예약발송시간 (HHMMSS)
REPORT_FLAG         VARCHAR(1)    -- 리포트 수신 여부
LGU_RESULT_CODE     VARCHAR(10)   -- LGU 응답 코드
LGU_MESSAGE_KEY     VARCHAR(50)   -- LGU 메시지 키
```

#### 2. LGU V2 테이블 (`TB_LGU_V2_SMS_MESSAGE`)
```sql
-- LGU V2 전용 필드들 (REST API 방식)
LGU_V2_TRANSACTION_ID VARCHAR(100) -- LGU V2 트랜잭션 ID
ACCESS_TOKEN          VARCHAR(500) -- OAuth 액세스 토큰
API_VERSION           VARCHAR(10)  -- API 버전 (2.0)
CONTENT_TYPE          VARCHAR(20)  -- SMS/LMS/MMS/RCS
PRIORITY              VARCHAR(10)  -- HIGH/NORMAL/LOW
TTL                   INTEGER      -- Time To Live (초)
DELIVERY_RECEIPT      VARCHAR(1)   -- 전송 확인 여부
WEBHOOK_URL           VARCHAR(500) -- 콜백 URL
CUSTOM_FIELDS         JSON         -- JSON 확장 필드
LGU_TRACKING_ID       VARCHAR(100) -- LGU 추적 ID
```

#### 3. MTS 테이블 (`TB_MTS_SMS_MESSAGE`)
```sql
-- MTS 전용 필드들
MTS_MESSAGE_ID    VARCHAR(50)  -- MTS 메시지 ID
TELECOM_CODE      VARCHAR(10)  -- 통신사 코드 (01:SKT, 02:KT, 03:LGU+)
MESSAGE_CODE      VARCHAR(10)  -- 메시지 코드 (01:SMS, 02:LMS, 03:MMS)
NATION_CODE       VARCHAR(5)   -- 국가번호 (82)
DEST_PHONE        VARCHAR(20)  -- MTS 포맷 수신번호
SEND_PHONE        VARCHAR(20)  -- MTS 포맷 발신번호
RESERVE_YN        VARCHAR(1)   -- 예약발송 여부
COMPANY_ID        VARCHAR(20)  -- 업체 ID
SERVICE_CD        VARCHAR(10)  -- 서비스 코드
BILL_TYPE         VARCHAR(5)   -- 과금 유형
ETC_NUM_1         VARCHAR(20)  -- 기타숫자1
ETC_NUM_2         VARCHAR(20)  -- 기타숫자2
ETC_STR_1         VARCHAR(100) -- 기타문자1
ETC_STR_2         VARCHAR(100) -- 기타문자2
MTS_RESULT_CODE   VARCHAR(10)  -- MTS 결과 코드
MTS_SEQ_NO        VARCHAR(20)  -- MTS 순번
```

### 채널 라우팅 로직

```java
// 서비스 코드 기반 채널 선택
if (serviceCode.startsWith("LGU")) {
    if (message.getContent().length() > 1000) {
        return MessageChannel.LGU_V2; // 긴 메시지는 V2
    }
    return MessageChannel.LGU_V1; // 기본값
}

if (serviceCode.startsWith("MTS")) {
    return MessageChannel.MTS;
}

return MessageChannel.LGU_V1; // 기본값
```

## 테스트

### 테스트 구조
프로젝트는 포괄적인 테스트 커버리지(80% 이상)를 목표로 다음과 같은 테스트를 포함합니다:

#### 1. 단위 테스트 (Unit Tests)
```bash
# SMS 모듈 단위 테스트
src/test/java/com/bwc/messaging/sms/
├── application/
│   ├── SmsApplicationServiceTest.java      # 비즈니스 로직 테스트
│   ├── SmsChannelRouterTest.java           # 채널 라우팅 테스트
│   └── strategy/
│       ├── SmsStrategyFactoryTest.java     # 전략 팩토리 테스트
│       └── impl/
│           └── LguV1SmsStrategyTest.java   # LGU V1 전략 테스트
├── domain/
│   └── SmsMessageTest.java                 # 도메인 객체 테스트
└── infrastructure/
    └── persistence/
        └── SmsRepositoryImplTest.java      # Repository 구현체 테스트
```

#### 2. 통합 테스트 (Integration Tests)
```bash
# 발송사별 데이터베이스 통합 테스트
src/test/java/com/bwc/messaging/sms/infrastructure/persistence/
└── SmsVendorRepositoryIntegrationTest.java  # H2 DB 실제 저장 테스트

# 발송사별 전략 통합 테스트
src/test/java/com/bwc/messaging/sms/application/strategy/
└── SmsVendorStrategyIntegrationTest.java    # 전략 + DB 통합 테스트
```

#### 3. End-to-End 테스트
```bash
# 전체 플로우 E2E 테스트
src/test/java/com/bwc/messaging/sms/application/
└── SmsApplicationServiceEndToEndTest.java   # 라우팅 → 전략 → DB 저장 전체 플로우
```

### 테스트 실행

#### 로컬 테스트 (H2 Database)
```bash
# 전체 테스트 실행
./gradlew test

# SMS 모듈만 테스트
./gradlew test --tests "com.bwc.messaging.sms.*"

# 특정 테스트 클래스 실행
./gradlew test --tests "SmsApplicationServiceTest"

# 테스트 커버리지 포함
./gradlew test jacocoTestReport
```

#### 통합 테스트 (실제 DB)
```bash
# 검증 환경에서 실제 DB 연동 테스트
./gradlew test -Dspring.profiles.active=integration

# 환경변수로 DB 정보 주입
LGU_V1_DB_USERNAME=real_user LGU_V1_DB_PASSWORD=real_pass ./gradlew test
```

### 테스트 설정

#### H2 테스트 설정 (`application-test.yml`)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

#### 실제 DB 테스트 설정 (`application-integration.yml`)
```yaml
spring:
  datasource:
    lgu-v1:
      jdbc-url: jdbc:oracle:thin:@lgu-v1-db:1521:LGUV1DB
      username: ${LGU_V1_DB_USERNAME}
      password: ${LGU_V1_DB_PASSWORD}
    lgu-v2:
      jdbc-url: jdbc:postgresql://lgu-v2-db:5432/lguv2db
      username: ${LGU_V2_DB_USERNAME}
      password: ${LGU_V2_DB_PASSWORD}
    mts:
      jdbc-url: jdbc:sqlserver://mts-db:1433;databaseName=mtsdb
      username: ${MTS_DB_USERNAME}
      password: ${MTS_DB_PASSWORD}
```

### 테스트 검증 항목

#### 발송사별 테이블 저장 검증
- ✅ **LGU V1**: `TB_LGU_V1_SMS_MESSAGE` 테이블에 LGU V1 전용 필드 저장
- ✅ **LGU V2**: `TB_LGU_V2_SMS_MESSAGE` 테이블에 LGU V2 전용 필드 저장  
- ✅ **MTS**: `TB_MTS_SMS_MESSAGE` 테이블에 MTS 전용 필드 저장

#### 채널 라우팅 검증
- ✅ **LGU 서비스 코드** → LGU V1/V2 채널 선택
- ✅ **메시지 길이** → LGU V1(짧음) vs V2(긺) 자동 선택
- ✅ **MTS 서비스 코드** → MTS 채널 선택
- ✅ **기본 서비스** → 기본 채널(LGU V1) 선택

#### 비즈니스 로직 검증
- ✅ **메시지 유효성 검증** (전화번호, 내용 길이 등)
- ✅ **상태 관리** (PENDING → SENT → DELIVERED)
- ✅ **재시도 로직** (실패 시 재발송)
- ✅ **에러 처리** (유효하지 않은 메시지, DB 오류 등)

## 설정

### Discord 연동
```json
{
  "type": "FACEBOOK_MESSENGER",
  "webhookUrl": "https://discord.com/api/webhooks/...",
  "content": "메시지 내용",
  "isEmbed": true
}
```

## 데이터베이스

### 기술 스택
- **JPA + Hibernate**: ORM 프레임워크
- **Spring Data JPA**: Repository 추상화
- **H2**: 로컬 개발 및 테스트용 인메모리 DB
- **Multi-Database Support**: 발송사별 전용 데이터베이스 지원

### 발송사별 테이블 전략
각 SMS 발송사는 독립적인 테이블을 사용하여 최적화된 데이터 구조를 제공합니다:

- **LGU V1**: 레거시 API 전용 필드 (예약발송, 광고구분 등)
- **LGU V2**: REST API 전용 필드 (OAuth, JSON 확장 등)  
- **MTS**: 통신사/과금 전용 필드 (업체코드, 과금타입 등)

### 환경별 데이터베이스 설정

#### 로컬 개발 (H2)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
```

#### 운영 환경 (멀티 DB)
```yaml
spring:
  datasource:
    lgu-v1:
      jdbc-url: jdbc:oracle:thin:@lgu-db:1521:LGUDB
    lgu-v2:  
      jdbc-url: jdbc:postgresql://lgu2-db:5432/lgu2db
    mts:
      jdbc-url: jdbc:sqlserver://mts-db:1433;databaseName=mtsdb
```