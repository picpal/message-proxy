package com.bwc.common;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

/**
 * TraceId MDC 테스트
 */
@Slf4j
@SpringBootTest
public class TraceIdIntegrationTest {

    @Test
    public void testMDCTraceId() {
        // MDC에 traceId 설정
        MDC.put("traceId", "test-trace-123");
        
        log.info("Testing MDC traceId functionality");
        log.debug("This is a debug message with traceId");
        log.warn("This is a warning message with traceId");
        
        // MDC 정리
        MDC.clear();
        
        log.info("Testing without traceId");
    }
}