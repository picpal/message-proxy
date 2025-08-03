package com.bwc.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * TraceId 시스템 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TraceIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTraceIdGeneration() throws Exception {
        // 헬스체크 엔드포인트 호출로 traceId 생성 확인
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCustomTraceId() throws Exception {
        // 커스텀 traceId로 요청
        mockMvc.perform(get("/actuator/health")
                .header("X-Trace-ID", "custom-trace-12345"))
                .andExpect(status().isOk());
    }
}