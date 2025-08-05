# Message Proxy

통합 메시징 시스템 - SMS, SNS, Email, Push 알림을 통합 관리하는 메시징 플랫폼

## 🏗️ 아키텍처 업데이트

**✅ 헥사고날 아키텍처 적용 완료** (SMS 도메인)
- **포트-어댑터 패턴**: 비즈니스 로직과 외부 의존성 완전 분리
- **큐 시스템 준비**: 새로운 어댑터 추가만으로 큐 도입 가능
- **테스트 용이성**: 포트를 Mock으로 교체하여 독립적인 단위 테스트
- **레거시 호환**: 기존 코드와 100% 호환성 유지

## 개발 환경

### spec
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

## 지원 메시지 타입

### SMS (단문/장문 메시지)
- **LGU V1**: DB 저장 방식, 알림톡 미지원, 해외발송 지원
- **LGU V2**: DB 저장 방식, 알림톡 지원 , 해외발송 미지원, LG U+ 백오피스 제공
- **MTS**: DB 저장 방식, 알림톡 지원, 해외발송 미지원

### SNS (소셜 네트워크 서비스)
- **Discord**: 웹훅 기반 메시지 발송, Embed 지원

### Email
- **SMTP**: 사내 SMTP서버 활용

## 아키텍처

### Hexagonal Architecture (포트-어댑터 패턴)

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
├── sms/                      # SMS 도메인 (헥사고날 아키텍처 적용)
│   ├── domain/              # 도메인 레이어 (순수 비즈니스 로직)
│   │   ├── SmsMessage.java  # SMS 메시지 도메인 객체
│   │   └── SmsRepository.java # 도메인 리포지토리 인터페이스
│   │
│   ├── application/         # 애플리케이션 레이어 (Use Case & Ports)
│   │   ├── port/           # 포트 인터페이스 정의
│   │   │   ├── in/         # 인바운드 포트 (Use Cases)
│   │   │   │   ├── SendSmsUseCase.java       # SMS 발송 Use Case
│   │   │   │   ├── GetSmsStatusUseCase.java  # SMS 상태 조회 Use Case
│   │   │   │   ├── RetrySmsUseCase.java      # SMS 재전송 Use Case
│   │   │   │   ├── SendSmsCommand.java       # SMS 발송 명령
│   │   │   │   ├── GetSmsStatusQuery.java    # SMS 상태 조회 쿼리
│   │   │   │   └── RetrySmsCommand.java      # SMS 재전송 명령
│   │   │   └── out/        # 아웃바운드 포트 (외부 의존성 추상화)
│   │   │       ├── SmsRepositoryPort.java    # 데이터 저장소 포트
│   │   │       └── SmsVendorPort.java        # SMS 벤더 포트
│   │   │
│   │   ├── service/        # 애플리케이션 서비스 (Use Case 구현)
│   │   │   └── SmsService.java               # SMS Use Case 구현체
│   │   │
│   │   ├── SmsChannelRouter.java      # 발송사별 채널 라우팅 (레거시)
│   │   └── strategy/        # 전략 패턴 구현 (레거시, Adapter에서 활용)
│   │       ├── SmsStrategy.java           # 전략 인터페이스
│   │       ├── SmsStrategyFactory.java    # 전략 팩토리
│   │       └── impl/        # 발송사별 전략 구현
│   │           ├── LguV1SmsStrategy.java  # LGU V1 전략
│   │           ├── LguV2SmsStrategy.java  # LGU V2 전략
│   │           └── MtsSmsStrategy.java    # MTS 전략
│   │
│   ├── adapter/            # 어댑터 레이어 (포트 구현체)
│   │   ├── in/            # 인바운드 어댑터 (외부 → 내부)
│   │   │   └── web/       # 웹 어댑터
│   │   │       ├── SmsWebAdapter.java        # HTTP 요청 처리
│   │   │       └── dto/   # 웹 계층 DTO
│   │   │           ├── SmsRequest.java       # SMS 발송 요청 DTO
│   │   │           └── SmsResponse.java      # SMS 발송 응답 DTO
│   │   │
│   │   └── out/           # 아웃바운드 어댑터 (내부 → 외부)
│   │       ├── persistence/  # 데이터 저장소 어댑터
│   │       │   └── SmsRepositoryAdapter.java # 레거시 Repository 래핑
│   │       └── vendor/    # 외부 벤더 어댑터
│   │           └── SmsVendorAdapter.java     # 레거시 Strategy 래핑
│   │
│   └── infrastructure/      # 인프라 레이어 (기술적 구현)
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

### 헥사고날 아키텍처 핵심 원칙

#### 1. **의존성 역전 (Dependency Inversion)**
```java
// ❌ 기존: Application이 Infrastructure에 직접 의존
@Service
public class SmsApplicationService {
    private final SmsRepository smsRepository;           // 구체 클래스 의존
    private final SmsStrategyFactory strategyFactory;   // 구체 클래스 의존
}

// ✅ 헥사고날: Application이 Port(추상화)에만 의존
@Service 
public class SmsService implements SendSmsUseCase {
    private final SmsRepositoryPort repositoryPort;  // 포트(인터페이스) 의존
    private final SmsVendorPort vendorPort;          // 포트(인터페이스) 의존
}
```

#### 2. **포트-어댑터 패턴 (Ports & Adapters)**

**인바운드 포트 (Use Cases)**: 외부에서 애플리케이션으로 들어오는 요청
- `SendSmsUseCase`: SMS 발송 비즈니스 로직
- `GetSmsStatusUseCase`: SMS 상태 조회 비즈니스 로직  
- `RetrySmsUseCase`: SMS 재전송 비즈니스 로직

**아웃바운드 포트**: 애플리케이션에서 외부로 나가는 요청
- `SmsRepositoryPort`: 데이터 저장소 추상화
- `SmsVendorPort`: 외부 SMS 벤더 추상화

**어댑터**: 포트의 구체적인 구현체
- `SmsWebAdapter`: HTTP 요청을 Use Case 호출로 변환
- `SmsRepositoryAdapter`: 레거시 Repository를 포트로 래핑
- `SmsVendorAdapter`: 레거시 Strategy 패턴을 포트로 래핑

#### 3. **큐 시스템 도입 준비 완료**

**WAS 앞단 큐 (API → Queue → WAS)**:
```java
// 새로운 어댑터만 추가, 기존 코드 변경 없음
@Component
public class SmsQueueAdapter {
    @RabbitListener(queues = "sms.queue")
    public void handleSms(SmsMessage message) {
        SendSmsCommand command = SendSmsCommand.from(message);
        sendSmsUseCase.sendSms(command);  // 기존 Use Case 재사용
    }
}
```

**WAS 뒤 큐 (WAS → Queue → 외부 API)**:
```java
// 새로운 어댑터만 추가, 기존 코드 변경 없음
@Component
public class QueuedSmsVendorAdapter implements SmsVendorPort {
    public MessageResult send(SmsMessage message) {
        rabbitTemplate.send("sms.vendor.queue", message);
        return MessageResult.pending(message.getMessageId());
    }
}
```

#### 4. **테스트 용이성**
```java
// 포트를 Mock으로 쉽게 교체 가능
@ExtendWith(MockitoExtension.class)
class SmsServiceTest {
    @Mock private SmsRepositoryPort repositoryPort;
    @Mock private SmsVendorPort vendorPort;
    
    @Test
    void testSendSms() {
        // Given
        when(vendorPort.send(any())).thenReturn(success());
        
        // When  
        MessageResult result = smsService.sendSms(command);
        
        // Then
        verify(repositoryPort).save(any());
        verify(vendorPort).send(any());
    }
}
```

#### 5. **기존 레거시 코드 완전 보존**
- ✅ **Domain Layer**: `SmsMessage`, `SmsRepository` 그대로 유지
- ✅ **Infrastructure Layer**: 모든 JPA 구현체 그대로 유지  
- ✅ **Strategy Pattern**: `SmsStrategy`, `SmsStrategyFactory` 재사용
- ✅ **Database Schema**: 모든 테이블 구조 그대로 유지

#### 6. **점진적 마이그레이션 지원**
```java
// 기존 API와 새로운 API 공존 가능
@Deprecated
@Service 
public class SmsApplicationService {  // 레거시 API 유지
    // 기존 코드...
}

@Service
public class SmsService implements SendSmsUseCase {  // 새로운 헥사고날 API
    // 새로운 구조...
}
```

#### 7. **설계 원칙 요약**
1. **Hexagonal Architecture**: 비즈니스 로직과 외부 의존성 완전 분리
2. **CQRS**: Command(변경)와 Query(조회) 분리된 인터페이스
3. **Strategy Pattern**: 발송사별 전략을 Adapter에서 재사용
4. **Facade Pattern**: 통합 API 진입점 유지 (`MessageFacade`)
5. **Domain-Driven Design**: 도메인별 경계 명확히 구분
6. **Multi-Vendor Support**: 발송사별 전용 테이블과 최적화된 데이터 구조

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

### 테스트 실행

#### 로컬 테스트 (H2 Database)
```bash
# 전체 테스트 실행
./gradlew test

# SMS 모듈만 테스트
./gradlew test --tests "com.bwc.messaging.sms.*"

# 헥사고날 아키텍처 테스트 실행
./gradlew test --tests "SmsServiceTest"

# 레거시 테스트 실행 (호환성 확인)
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

#### 헥사고날 아키텍처 검증
- ✅ **Use Case 테스트**: `SmsServiceTest` - 모든 Use Case 시나리오 검증
- ✅ **포트 의존성**: Application Core가 포트(인터페이스)에만 의존하는지 검증
- ✅ **어댑터 분리**: Inbound/Outbound 어댑터가 올바르게 포트를 구현하는지 검증
- ✅ **Mock 테스트**: 포트를 Mock으로 교체하여 단위 테스트 가능한지 검증
- ✅ **의존성 역전**: Infrastructure가 Application에 의존하는지 검증

#### Use Case별 검증
- ✅ **SendSmsUseCase**: SMS 발송 명령 처리 및 결과 반환
- ✅ **GetSmsStatusUseCase**: SMS 상태 조회 쿼리 처리
- ✅ **RetrySmsUseCase**: SMS 재전송 명령 처리
- ✅ **Command/Query 분리**: 변경과 조회 인터페이스 분리 검증

#### 어댑터 검증
- ✅ **SmsWebAdapter**: HTTP 요청을 Command/Query로 변환
- ✅ **SmsRepositoryAdapter**: 레거시 Repository를 포트로 래핑
- ✅ **SmsVendorAdapter**: 레거시 Strategy를 포트로 래핑

#### 발송사별 테이블 저장 검증 (레거시 호환성)
- ✅ **LGU V1**: `TB_LGU_V1_SMS_MESSAGE` 테이블에 LGU V1 전용 필드 저장
- ✅ **LGU V2**: `TB_LGU_V2_SMS_MESSAGE` 테이블에 LGU V2 전용 필드 저장  
- ✅ **MTS**: `TB_MTS_SMS_MESSAGE` 테이블에 MTS 전용 필드 저장

#### 채널 라우팅 검증 (레거시 호환성)
- ✅ **LGU 서비스 코드** → LGU V1/V2 채널 선택
- ✅ **메시지 길이** → LGU V1(짧음) vs V2(긺) 자동 선택
- ✅ **MTS 서비스 코드** → MTS 채널 선택
- ✅ **기본 서비스** → 기본 채널(LGU V1) 선택

#### 비즈니스 로직 검증
- ✅ **메시지 유효성 검증** (전화번호, 내용 길이 등)
- ✅ **상태 관리** (PENDING → SENT → DELIVERED)
- ✅ **재시도 로직** (실패 시 재발송)
- ✅ **에러 처리** (유효하지 않은 메시지, DB 오류 등)
- ✅ **메시지 타입 자동 결정** (SMS → LMS 변환)

#### 통합 테스트 검증
- ✅ **서버 시작**: Spring Boot 애플리케이션 정상 시작
- ✅ **Bean 주입**: 모든 헥사고날 컴포넌트 의존성 주입 성공
- ✅ **API 호출**: 새로운 SMS API 엔드포인트 정상 작동
- ✅ **레거시 호환**: 기존 통합 API(`MessageFacade`) 정상 작동

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
