package com.bwc.message.v1.gate.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bwc.common.crypt.KeyGenerator;
import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;
import com.bwc.message.v1.gate.service.MessageGateService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1")
public class MessageGateContorller {

	private final MessageGateService messageGateService;
	private final KeyGenerator keyGenerator;

	public MessageGateContorller(MessageGateService messageGateService, KeyGenerator keyGenerator) {
		this.messageGateService = messageGateService;
		this.keyGenerator = keyGenerator;

	}

	/**
	 * 메시지 발송
	 *
	 * @param messageGateSendReqDTO
	 * @param model
	 * @return void
	 * @throws Exception
	 * */
	@PostMapping("/send")
	@ResponseBody
	public ResponseEntity<?> send(@RequestBody @Valid MessageGateSendReqDTO messageGateSendReqDTO,
		HttpSession session) throws
		Exception {
		MessageGateResDTO result = messageGateService.requestSendMessage(messageGateSendReqDTO, session);
		return ResponseEntity.ok(result);
	}

	/**
	 * 메시지 발송상태 조회(단건)
	 *
	 * @param messageGateSendReqDTO
	 * @param model
	 * @return void
	 * @throws Exception
	 * */
	@PostMapping("/status")
	@ResponseBody
	public ResponseEntity<?> status(@RequestBody @Valid MessageGateStatusReqDTO messageGateStatusReqDTO,
		HttpSession session) throws
		Exception {
		MessageGateStatusResDTO result = messageGateService.getStatus(messageGateStatusReqDTO, session);

		return ResponseEntity.ok(result);
	}

	/**
	 * 메시지 발송내역 조회
	 *
	 * @param messageGateSendReqDTO
	 * @param model
	 * @return void
	 * @throws Exception
	 * */
	@PostMapping("/history")
	@ResponseBody
	public ResponseEntity<?> history(@ModelAttribute MessageGateSendReqDTO messageGateSendReqDTO,
		HttpSession session) throws
		Exception {
		messageGateService.getSendHistory(messageGateSendReqDTO, session);
		return ResponseEntity.ok(Collections.singletonMap("result", "success"));
	}

	/**
	 * API key 발급 테스트용. 
	 * @TODO 백오피스 기능으로 제거해야함.
	 *
	 * @param messageGateSendReqDTO
	 * @param model
	 * @return void
	 * @throws Exception
	 * */
	@PostMapping("/apikey")
	@ResponseBody
	public ResponseEntity<?> generateKey(@RequestBody Map params,
		HttpSession session) throws
		Exception {

		Map result = keyGenerator.createService(params);

		return ResponseEntity.ok(result);
	}
}
