# Common/Util 패키지 테스트 가이드 📋

## 🎯 개요
Common/Util 패키지의 모든 유틸리티 클래스에 대한 포괄적인 테스트 케이스를 작성했습니다. 실제 사용 시나리오에 맞춘 현실적이고 실용적인 테스트를 제공합니다.

## 📦 테스트 대상 유틸리티 클래스

### 1. **DateUtil** - 날짜/시간 처리 유틸리티
```java
// 현재 날짜/시간 관련
DateUtil.getYyyymmdd()        // 20231225
DateUtil.getYyyymm()          // 202312  
DateUtil.getYyyymmddhhmmss()  // 20231225143025
DateUtil.getMillisecTime()    // 20231225143025123

// 날짜 계산
DateUtil.getAddDay("20231220", 5)     // 20231225
DateUtil.getAddMonth("20231020", 2)   // 20231220

// 유효성 검사
DateUtil.isDate("20231225")           // true
DateUtil.isTime("143025")             // true

// 형식 변환
DateUtil.getStringToDateForm("20231225")  // 2023-12-25
DateUtil.formatDate("20231225", "yyyy-MM-dd")  // 2023-12-25
```

### 2. **NtnoCrtnUtils** - GUID 생성 유틸리티
```java
// GUID 생성
ntnoCrtnUtils.occrTrxGuid()      // TEST112023122514302512301
ntnoCrtnUtils.occrSessionGuid()  // TEST11SG231225143025123401

// 내부대체식별번호
ntnoCrtnUtils.getInnrTrfrIdno("A")  // A20231225143025123451

// 유틸리티 메서드
NtnoCrtnUtils.systemPrefix()     // 11, 12, 21, 22 중 하나
NtnoCrtnUtils.occrRmno(5)        // 12345 (5자리 난수)
NtnoCrtnUtils.occrSqnc(2)        // 01 (2자리 시퀀스)
```

### 3. **AESCryptUtil** - AES 암호화 유틸리티
```java
// 기본 암호화/복호화
AESCryptUtil aes = new AESCryptUtil("32자리키...");
String encrypted = aes.encrypt("Hello, World!");
String decrypted = aes.decrypt(encrypted);  // Hello, World!

// 다양한 데이터 타입
aes.encrypt("안녕하세요");                    // 한글
aes.encrypt("{\"name\":\"test\"}");          // JSON
aes.encrypt("🚀 이모지도 가능 🎉");             // 이모지
```

## 🧪 테스트 클래스 구조

### **UtilTestSuite** - 통합 테스트 (권장)
실제 사용 시나리오에 맞춘 현실적인 테스트:

```java
@SpringBootTest
@TestPropertySource(properties = {"trx.guid.prifix=TEST"})
class UtilTestSuite {
    
    @Test void testDateUtilBasicFunctions()        // DateUtil 기본 기능
    @Test void testNtnoCrtnUtilsGuidGeneration()   // GUID 생성
    @Test void testAESCryptUtilBasicFunctions()    // 암호화 기본
    @Test void testUtilityClassesCooperation()     // 유틸리티 간 협업
    @Test void testPerformanceMassGuidGeneration() // 성능 테스트
    @Test void testPerformanceMassEncryption()     // 암호화 성능
}
```

### **개별 테스트 클래스들**
각 유틸리티별 상세 테스트:

- **DateUtilTest** - 날짜 유틸리티 포괄적 테스트 (80+ 테스트)
- **NtnoCrtnUtilsTest** - GUID 생성 상세 테스트 (시퀀스, 동시성 등)
- **AESCryptUtilTest** - 암호화 상세 테스트 (예외 처리, 성능 등)

## 🚀 테스트 실행 방법

### 전체 테스트 실행
```bash
./gradlew test --tests="com.bwc.common.util.*"
```

### 통합 테스트만 실행 (빠른 검증)
```bash
./gradlew test --tests="com.bwc.common.util.UtilTestSuite"
```

### 개별 클래스 테스트
```bash
./gradlew test --tests="com.bwc.common.util.DateUtilTest"
./gradlew test --tests="com.bwc.common.util.NtnoCrtnUtilsTest"
./gradlew test --tests="com.bwc.common.util.AESCryptUtilTest"
```

## 📊 테스트 커버리지

### **DateUtil** 테스트 범위
✅ **현재 날짜/시간** - 모든 포맷 (YYYY, YYYYMM, YYYYMMDD, etc.)  
✅ **날짜 계산** - 일/월/년 더하기/빼기  
✅ **날짜 변환** - String ↔ Timestamp, 다양한 포맷  
✅ **유효성 검사** - 날짜/시간 유효성 확인  
✅ **Calendar 연산** - 레거시 Calendar API 지원  
✅ **Java 8 Time API** - 현대적 날짜 API 활용  
✅ **예외 처리** - null, 잘못된 형식 등  

### **NtnoCrtnUtils** 테스트 범위
✅ **GUID 생성** - 거래/세션/내부대체식별번호  
✅ **시퀀스 관리** - 순차 증가, 자동 리셋 (90 초과 시)  
✅ **난수 생성** - 지정 자릿수 난수  
✅ **시스템 프리픽스** - 서버 환경별 구분  
✅ **동시성** - 멀티스레드 환경 안전성  
✅ **유일성** - GUID 중복 방지  
✅ **설정 반영** - application.yml 프로퍼티 적용  

### **AESCryptUtil** 테스트 범위
✅ **기본 암호화/복호화** - 표준 문자열  
✅ **다국어 지원** - 한글, 특수문자, 이모지  
✅ **다양한 데이터** - JSON, XML, 긴 텍스트  
✅ **키 길이 처리** - 32자 미만/초과 키  
✅ **예외 처리** - 잘못된 암호문, null 값  
✅ **성능 테스트** - 대량 암호화 처리  
✅ **메모리 관리** - 메모리 누수 방지  

## 🛡️ 보안 테스트 고려사항

### **AESCryptUtil 보안 이슈**
⚠️ **하드코딩된 IV**: 보안상 문제 (현재 구현 상태)  
⚠️ **동일 암호문 생성**: IV 고정으로 인한 패턴 노출  
⚠️ **키 로깅**: merchantKey가 로그에 노출될 가능성  

### **테스트에서의 보안 배려**
✅ **테스트 전용 키 사용**: 실제 운영 키 사용 금지  
✅ **민감 정보 제외**: 실제 개인정보나 기밀 데이터 사용 안함  
✅ **예외 처리 확인**: 보안 예외 상황 테스트  

## 🎨 테스트 베스트 프랙티스

### **1. 현실적 테스트**
```java
// ❌ 이론적 테스트
@Test void testAllPossibleDateFormats() { ... }

// ✅ 실용적 테스트  
@Test void testCommonDateOperations() {
    assertThat(DateUtil.getAddDay("20231220", 5)).isEqualTo("20231225");
}
```

### **2. 협업 테스트**
```java
// 유틸리티 클래스들이 함께 사용되는 시나리오
@Test void testUtilityClassesCooperation() {
    String trxGuid = ntnoCrtnUtils.occrTrxGuid();
    String encrypted = aesUtil.encrypt(trxGuid);
    assertThat(aesUtil.decrypt(encrypted)).isEqualTo(trxGuid);
}
```

### **3. 성능 테스트**
```java
@Test void testPerformance() {
    long startTime = System.currentTimeMillis();
    // 대량 작업 수행
    long duration = System.currentTimeMillis() - startTime;
    assertThat(duration).isLessThan(5000L); // 5초 이내
}
```

## 📈 지속적 개선

### **테스트 확장 포인트**
1. **AES 보안 강화**: IV 랜덤화, 키 관리 개선
2. **날짜 타임존**: 다양한 타임존 지원 테스트
3. **GUID 성능**: 더 효율적인 생성 알고리즘
4. **메모리 최적화**: 대용량 처리 시 메모리 사용량

### **테스트 자동화**
```bash
# CI/CD 파이프라인에서 자동 실행
./gradlew test jacocoTestReport
# 테스트 커버리지 리포트 생성: build/reports/jacoco/test/html/index.html
```

---

이제 Common/Util 패키지의 모든 기능이 철저히 테스트되어 안정적인 개발과 운영이 가능합니다! 🎉