package com.bwc.common.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 모든 HTTP 요청에 대해 traceId를 생성하고 MDC에 설정하는 필터
 * 
 * - 요청 헤더에서 X-Trace-ID가 있으면 사용, 없으면 새로 생성
 * - MDC를 통해 모든 로그에 traceId 자동 포함
 * - 요청 완료 후 MDC 정리
 */
@Slf4j
@Component
@Order(1) // 가장 먼저 실행
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String REQUEST_URI_KEY = "requestUri";
    private static final String METHOD_KEY = "method";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // traceId 설정
            String traceId = getOrGenerateTraceId(httpRequest);
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(REQUEST_URI_KEY, httpRequest.getRequestURI());
            MDC.put(METHOD_KEY, httpRequest.getMethod());
            
            // 헬스체크나 액추에이터 엔드포인트가 아닌 경우에만 로깅
            if (shouldLog(httpRequest)) {
                log.info("HTTP {} {} started", httpRequest.getMethod(), httpRequest.getRequestURI());
            }
            
            chain.doFilter(request, response);
            
        } finally {
            // 요청 완료 후 MDC 정리
            if (shouldLog(httpRequest)) {
                log.info("HTTP {} {} completed", httpRequest.getMethod(), httpRequest.getRequestURI());
            }
            MDC.clear();
        }
    }

    /**
     * 헤더에서 traceId를 가져오거나 새로 생성
     */
    private String getOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId != null && !traceId.trim().isEmpty()) {
            return traceId.trim();
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 로깅 대상인지 확인
     */
    private boolean shouldLog(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        // 헬스체크, 액추에이터, 정적 리소스는 로깅 제외
        return !uri.startsWith("/actuator") && 
               !uri.equals("/health") &&
               !uri.startsWith("/static") &&
               !uri.startsWith("/css") &&
               !uri.startsWith("/js") &&
               !uri.startsWith("/images") &&
               !uri.equals("/favicon.ico");
    }
}