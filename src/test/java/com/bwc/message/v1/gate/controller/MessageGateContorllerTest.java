package com.bwc.message.v1.gate.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.bwc.common.util.DateUtil;
import com.bwc.common.util.StrUtil;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
class MessageGateContorllerTest {
	@Autowired
	protected WebApplicationContext ctx;

	@Autowired
	protected ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@Autowired
	MessageGateContorller messageGateContorller;

	private String SEND_UID;
	private static Map<String, String> SEND_UIDS = new HashMap<>();

	// 테스트 데이터 초기화
	private void initData() {
		String sendUid = "TEST_" + DateUtil.getDate("yyyyMMddHHmmss") + StrUtil.getRandomNumText(4);

		SEND_UID = sendUid;

	}

	@BeforeEach
	void setUp(final RestDocumentationContextProvider restDocumentaion) {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
			.apply(documentationConfiguration(restDocumentaion))
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();

		initData();
	}

	@Order(1)
	@DisplayName("메시지 발송")
	@ParameterizedTest
	@CsvSource({"01, SMS"})
	void MG_TEST_01_SEND(String serviceCode, String sendType) throws Exception {
		Map<String, String> params = new HashMap<>();

		params.put("sendUid", SEND_UID);
		params.put("sendType", sendType); // SMS, MMS
		params.put("receiverPhone", "01096771739");
		params.put("senderNumber", "15224910");
		params.put("subject", "발송 TEST TITLE");
		params.put("sendMsg", "Hello Service !! serviceCode : [" + serviceCode + "] , sendType : [" + sendType + "]");
		params.put("serviceCode", serviceCode);

		SEND_UIDS.put(serviceCode, SEND_UID);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonContent = objectMapper.writeValueAsString(params);

		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/v1/send")
				.content(jsonContent)
				// .with(httpBasic("", ""))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andDo(
			MockMvcRestDocumentationWrapper.document("v1/test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				resource(
					ResourceSnippetParameters.builder()
						.description("메시지 발송")
						.requestFields(
							fieldWithPath("sendUid").description("메시지 발송일자"),
							fieldWithPath("sendType").description("메시지 발송일자"),
							fieldWithPath("receiverPhone").description("메시지 발송일자"),
							fieldWithPath("senderNumber").description("메시지 발송일자"),
							fieldWithPath("subject").description("메시지 발송일자"),
							fieldWithPath("sendMsg").description("서비스 분류 코드"),
							fieldWithPath("serviceCode").description("서비스 고유키")
						)
						.responseFields(
							fieldWithPath("resultCode").description("결과 값"),
							fieldWithPath("resultMsg").description("결과 메시지")
						)
						.build()
				)
			)
		);

		resultActions.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@Order(2)
	@DisplayName("메시지 발송 후 발송이력 insert 시간 대기")
	void MG_TEST_WAIT() throws Exception {
		Thread.sleep(5 * 1000);
	}

	@Order(3)
	@DisplayName("메시지 발송 상태 확인")
	@ParameterizedTest
	@ValueSource(strings = {"01"})
	void MG_TEST_02_STATUS(String serviceCode) throws Exception {
		Map<String, String> params = new HashMap<>();

		log.info("################ STATUS sendUid : {}", SEND_UID);
		params.put("sendUid", SEND_UIDS.get(serviceCode));
		params.put("sendDate", DateUtil.getYyyymmdd());
		params.put("serviceCode", serviceCode);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonContent = objectMapper.writeValueAsString(params);

		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/v1/status")
				.content(jsonContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andDo(
			MockMvcRestDocumentationWrapper.document("v1/test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				resource(
					ResourceSnippetParameters.builder()
						.description("메시지 발송 상태 확인")
						.requestFields(
							fieldWithPath("sendDate").description("메시지 발송일자"),
							fieldWithPath("serviceCode").description("서비스 분류 코드"),
							fieldWithPath("sendUid").description("서비스 고유키")
						)
						.responseFields(
							fieldWithPath("sessGuid").description("Message 발송 고유키"),
							fieldWithPath("sendUid").description("서비스 고유키"),
							fieldWithPath("sendDate").description("발송 일자"),
							fieldWithPath("status").description("발송 상태"),
							fieldWithPath("vender").description("발송 업체"),
							fieldWithPath("sendType").description("발송 유형"),
							fieldWithPath("serviceCode").description("서비스 분류 코드"),
							fieldWithPath("resultCode").description("결과 값"),
							fieldWithPath("resultMsg").description("결과 메시지")
						)
						.build()
				)
			)
		);

		resultActions.andExpect(MockMvcResultMatchers.status().isOk());
	}

}