# 추적 시스템 (Tracing System) 구현 완료

## 🎯 개요
MDC(Mapped Diagnostic Context) 기반의 traceId 추적 시스템을 구축하여 모든 로그에 추적 ID가 자동으로 포함되도록 구현했습니다.

## 📋 구현된 컴포넌트

### 1. **TraceIdFilter** - HTTP 요청 추적
- **위치**: `com.bwc.common.filter.TraceIdFilter`
- **기능**:
  - 모든 HTTP 요청에 대해 traceId 자동 생성
  - `X-Trace-ID` 헤더가 있으면 해당 값 사용, 없으면 UUID 기반 8자리 생성
  - MDC에 traceId, requestUri, method 설정
  - 요청 완료 후 MDC 자동 정리

### 2. **TraceableAspect** - 메서드 레벨 추적
- **위치**: `com.bwc.common.aspect.TraceableAspect`
- **기능**:
  - 컨트롤러 레이어: 요청/응답 상세 로깅
  - 서비스 레이어: 내부 서비스 호출 추적
  - 인프라 레이어: 외부 API 호출 추적
  - 성능 모니터링 및 디버깅 정보 제공

### 3. **MessageGateAspect** - 예외 처리 추적
- **위치**: `com.bwc.common.aspect.MessageGateAspect`
- **기능**:
  - 예외 핸들러 실행 추적
  - 에러 응답 모니터링
  - 기존 레거시 코드 제거 및 최적화

### 4. **로그 패턴 업데이트**
- **위치**: `src/main/resources/logback-local.xml`
- **변경사항**:
  ```xml
  <pattern>%d{HH:mm:ss.SSS} [%X{traceId:-}] [%thread] %-5level %logger{36} - %msg%n</pattern>
  ```

## 🔧 사용법

### HTTP 헤더로 traceId 전달
```bash
curl -H "X-Trace-ID: my-custom-trace" http://localhost:8080/api/messages/send
```

### 코드에서 traceId 접근
```java
import org.slf4j.MDC;

public class MyService {
    public void myMethod() {
        String traceId = MDC.get("traceId");
        log.info("Processing with traceId: {}", traceId);
    }
}
```

## 📊 로그 출력 예시

### 일반 로그
```
18:30:45.123 [a1b2c3d4] [http-nio-8080-exec-1] INFO  c.b.m.g.p.c.UnifiedMessageController - Received message send request: type=SMS, messageId=msg001
```

### AOP 로그
```
18:30:45.124 [a1b2c3d4] [http-nio-8080-exec-1] INFO  c.b.c.a.TraceableAspect - === REQUEST START === [a1b2c3d4] UnifiedMessageController.sendMessage
18:30:45.125 [a1b2c3d4] [http-nio-8080-exec-1] DEBUG c.b.c.a.TraceableAspect - >>> SERVICE CALL [a1b2c3d4] MessageFacade.sendMessage
18:30:45.126 [a1b2c3d4] [http-nio-8080-exec-1] INFO  c.b.c.a.TraceableAspect - >>> EXTERNAL CALL [a1b2c3d4] SmtpEmailClient.send
18:30:45.130 [a1b2c3d4] [http-nio-8080-exec-1] INFO  c.b.c.a.TraceableAspect - === REQUEST END === [a1b2c3d4] UnifiedMessageController.sendMessage
```

## 🛡️ 보안 고려사항

1. **민감한 정보 제외**: 응답 로깅에서 민감한 데이터 자동 필터링
2. **로그 레벨 제어**: DEBUG 레벨에서만 상세 정보 출력
3. **메모리 관리**: 요청 완료 후 MDC 자동 정리

## 🚀 성능 최적화

1. **필터링**: 헬스체크, 정적 리소스 등 불필요한 로깅 제외
2. **조건부 로깅**: 필요한 경우에만 상세 로깅 활성화
3. **메모리 효율**: UUID 8자리로 traceId 크기 최소화

## 🔄 호환성

- **기존 코드**: 기존 로깅 코드 수정 없이 자동으로 traceId 포함
- **외부 시스템**: X-Trace-ID 헤더로 분산 추적 지원
- **모니터링**: ELK Stack, Prometheus 등과 연동 가능

## 📈 모니터링 혜택

1. **요청 추적**: 단일 요청의 전체 처리 과정 추적 가능
2. **성능 분석**: 구간별 처리 시간 측정
3. **오류 디버깅**: 특정 요청의 오류 발생 지점 정확히 파악
4. **분산 추적**: 마이크로서비스 간 요청 흐름 추적

---

이제 모든 로그에 traceId가 자동으로 포함되어 효율적인 로그 추적이 가능합니다! 🎉