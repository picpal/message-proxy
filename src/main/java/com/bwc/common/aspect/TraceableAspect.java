package com.bwc.common.aspect;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 통합 메시징 시스템을 위한 추적 가능한 AOP
 * 
 * - MDC 기반 traceId 생성 및 관리
 * - 요청/응답 로깅
 * - 성능 모니터링
 */
@Slf4j
@Aspect
@Component
public class TraceableAspect {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String REQUEST_URI_KEY = "requestUri";
    private static final String USER_ID_KEY = "userId";
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 모든 컨트롤러 메서드
     */
    @Pointcut("execution(* com.bwc.messaging.gateway.presentation.controller.*.*(..))")
    public void controllerLayer() {
    }

    /**
     * 모든 서비스 메서드 (메시징 관련)
     */
    @Pointcut("execution(* com.bwc.messaging..application..*(..))")
    public void serviceLayer() {
    }

    /**
     * 모든 인프라스트럭처 레이어 (외부 API 호출 등)
     */
    @Pointcut("execution(* com.bwc.messaging..infrastructure..*(..))")
    public void infrastructureLayer() {
    }

    /**
     * 컨트롤러 메서드 실행 전 - traceId 생성 및 요청 로깅
     */
    @Before("controllerLayer()")
    public void beforeController(JoinPoint joinPoint) {
        String traceId = generateTraceId();
        MDC.put(TRACE_ID_KEY, traceId);
        
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                String requestUri = request.getRequestURI();
                MDC.put(REQUEST_URI_KEY, requestUri);
                
                // Authorization에서 사용자 정보 추출 (필요시)
                String authorization = request.getHeader("Authorization");
                if (authorization != null) {
                    // JWT 토큰에서 사용자 ID 추출 로직을 여기에 추가
                    MDC.put(USER_ID_KEY, "system"); // 기본값
                }
            }
            
            log.info("=== REQUEST START === [{}] {}.{}", 
                traceId,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
                
        } catch (Exception e) {
            log.warn("Failed to set request context: {}", e.getMessage());
        }
    }

    /**
     * 컨트롤러 메서드 실행 후 - 응답 로깅 및 MDC 정리
     */
    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void afterController(JoinPoint joinPoint, Object result) {
        try {
            String traceId = MDC.get(TRACE_ID_KEY);
            
            log.info("=== REQUEST END === [{}] {}.{}", 
                traceId,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
                
            // 응답 데이터 로깅 (민감한 정보 제외)
            if (result != null && shouldLogResponse(result)) {
                try {
                    String responseJson = objectMapper.writeValueAsString(result);
                    String truncatedResponse = truncateLog(responseJson, 500);
                    log.debug("=== RESPONSE DATA === [{}] {}", traceId, truncatedResponse);
                } catch (Exception e) {
                    log.debug("=== RESPONSE DATA === [{}] {}", traceId, result.getClass().getSimpleName());
                }
            }
            
        } catch (Exception e) {
            log.warn("Failed to log response: {}", e.getMessage());
        } finally {
            // MDC 정리
            MDC.clear();
        }
    }

    /**
     * 서비스 레이어 메서드 실행 전 - 내부 서비스 호출 로깅
     */
    @Before("serviceLayer()")
    public void beforeService(JoinPoint joinPoint) {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId != null) {
            log.debug(">>> SERVICE CALL [{}] {}.{}", 
                traceId,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        }
    }

    /**
     * 인프라스트럭처 레이어 메서드 실행 전 - 외부 API 호출 로깅
     */
    @Before("infrastructureLayer()")
    public void beforeInfrastructure(JoinPoint joinPoint) {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId != null) {
            log.info(">>> EXTERNAL CALL [{}] {}.{}", 
                traceId,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        }
    }

    /**
     * traceId 생성
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 현재 HTTP 요청 가져오기
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 응답 데이터 로깅 여부 결정
     */
    private boolean shouldLogResponse(Object result) {
        if (result == null) return false;
        
        String className = result.getClass().getSimpleName();
        // String 타입이나 기본 타입은 로깅하지 않음
        return !className.equals("String") && 
               !className.equals("ResponseEntity") ||
               log.isDebugEnabled();
    }

    /**
     * 로그 문자열 자르기
     */
    private String truncateLog(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}