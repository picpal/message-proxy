package com.bwc.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 예외 처리 전용 Aspect
 * 
 * - 예외 발생 시 로깅
 * - 에러 응답 모니터링
 */
@Slf4j
@Aspect
@Component
public class MessageGateAspect {

	@Pointcut("execution(* com.bwc.common.controller.MessageGateExceptionHandler.*(..))")
	public void exceptionHandlerLayer() {
	}

	/**
	 * 예외 처리 후 로깅
	 *
	 * @param joinpoint
	 * @param returnValue
	 * @throws Exception
	 */
	@AfterReturning(pointcut = "exceptionHandlerLayer()", returning = "returnValue")
	public void exceptionLog(JoinPoint joinpoint, Object returnValue) throws Exception {
		String traceId = MDC.get("traceId");
		
		log.warn("=== EXCEPTION HANDLED === [{}] {}.{} -> {}", 
			traceId,
			joinpoint.getSignature().getDeclaringTypeName(),
			joinpoint.getSignature().getName(),
			returnValue != null ? returnValue.getClass().getSimpleName() : "null");
			
		// 예외 응답 세부 정보는 DEBUG 레벨에서만 출력
		if (log.isDebugEnabled() && returnValue != null) {
			log.debug("=== EXCEPTION RESPONSE === [{}] {}", traceId, returnValue.toString());
		}
	}

}
