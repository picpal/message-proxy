package com.bwc.message.v1.gate.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.exception.MessageGateException;
import com.bwc.common.util.NtnoCrtnUtils;
import com.bwc.message.v1.gate.dao.MaMessageDAO;
import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;
import com.bwc.message.v1.gate.service.MessageGateService;
import com.bwc.message.v1.sender.service.MessageService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageGateServiceImpl implements MessageGateService {
	private final MessageService messageService;
	private final MaMessageDAO maMessageDAO;

	private final NtnoCrtnUtils ntnoCrtnUtils;

	@Autowired
	public MessageGateServiceImpl(MessageService messageService, MaMessageDAO maMessageDAO,
		NtnoCrtnUtils ntnoCrtnUtils) {
		this.messageService = messageService;
		this.maMessageDAO = maMessageDAO;
		this.ntnoCrtnUtils = ntnoCrtnUtils;
	}

	@Override
	public MessageGateResDTO requestSendMessage(MessageGateSendReqDTO messageGateSendReqDTO, HttpSession session) throws
		Exception {
		log.info("####### 1. Request Parameter , params : {} ", messageGateSendReqDTO.toString());

		log.info("####### 2. 초기 파라미터 설정");
		Map<String, String> initData = getInitData(messageGateSendReqDTO.getServiceCode(), session);
		messageGateSendReqDTO.setVender(initData.get("messageVender"));
		messageGateSendReqDTO.setSessGuid(initData.get("sessGuid"));

		log.info("####### 3. 메시지 전송");
		MessageGateResDTO result = messageService.sendMessage(messageGateSendReqDTO);

		return result;
	}

	@Override
	public MessageGateStatusResDTO getStatus(MessageGateStatusReqDTO messageGateStatusReqDTO,
		HttpSession session) throws
		Exception {
		log.info("####### 1. Request Parameter , params : {}", messageGateStatusReqDTO);

		log.info("####### 2. 초기 파라미터 설정");
		Map<String, String> initData = getInitData(messageGateStatusReqDTO.getServiceCode(), session);
		messageGateStatusReqDTO.setVender(initData.get("messageVender"));
		messageGateStatusReqDTO.setSessGuid(initData.get("sessGuid"));

		log.info("####### 3. MessageGate의 발송 정보 조회");
		MessageGateStatusResDTO sendMessageInfo = maMessageDAO.selectSendMessageInfo(messageGateStatusReqDTO);
		if (sendMessageInfo == null) {
			throw new MessageGateException(ErrorCodeEnum.BAD_REQUEST);
		}
		String status = sendMessageInfo.getStatus();
		if (!"01".equals(status)) { // 발송 상태가 대기중이 아닐때 Message Gate의 발송상태 반환
			return sendMessageInfo;
		}
		log.info("####### 3. RESULT : {}", sendMessageInfo.toString());

		log.info("####### 4. 발송 상태 [{}]. 업체의 발송 상태 확인 [{}]", sendMessageInfo.getStatus(), sendMessageInfo.getVender());
		MessageGateStatusReqDTO params = new MessageGateStatusReqDTO();
		params = params.setStatusParams(sendMessageInfo);
		MessageGateStatusResDTO result = messageService.getStatus(params);
		log.info("####### 4. RESULT : {}", result.toString());

		return result;
	}

	@Override
	public List<MessageGateResDTO> getSendHistory(MessageGateSendReqDTO messageGateSendReqDTO,
		HttpSession session) throws
		Exception {
		log.info("####### 1. Request Parameter Validate , params : {}", messageGateSendReqDTO);
		// boolean isValid = validataParams(messageGateReqDTO);
		// if (!isValid) {
		//
		// }

		log.info("####### 2. 초기 파라미터 설정");
		Map<String, String> initData = getInitData(messageGateSendReqDTO.getServiceCode(), session);
		messageGateSendReqDTO.setSessGuid(initData.get("sessGuid"));
		messageGateSendReqDTO.setVender(initData.get("messageVender"));

		log.info("####### 3. 메시지 전송");
		messageService.sendMessage(messageGateSendReqDTO);

		log.info("============= 4. 결과 데이터  =============");
		MessageGateResDTO messageGateResDTO = new MessageGateResDTO();
		messageGateResDTO.builder()
			.resultCode("")
			.resultMsg("")
			.build();

		return null;
	}

	/**
	 * BWC 서비스와 맵핑된 Message 서비스 업체 조회
	 *
	 * @param messageGateSendReqDTO 메세지 서비스 요청 파라미터
	 * */
	private String getMessageVender(String serviceCode) throws Exception {
		String messageType = maMessageDAO.selectMessageMapping(serviceCode);

		// default : MTS
		if (messageType == null || "".equals(messageType)) {
			return "mts";
		}

		return messageType;
	}

	/**
	 * 초기 파라미터 설정 [발송업체,세션ID]
	 *
	 * @param messageGateSendReqDTO 메세지 서비스 요청 파라미터
	 * */
	private Map<String, String> getInitData(String serviceCode, HttpSession session) throws
		Exception {
		String messageVender = getMessageVender(serviceCode);
		String sessGuid = (String)session.getAttribute("sessGuid");

		if (sessGuid == null) {
			throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}

		Map result = new HashMap();
		result.put("messageVender", messageVender);
		result.put("sessGuid", sessGuid);
		return result;
	}

}
