package com.bwc.message.v1.sender.service;

import java.util.List;

import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;

public interface MessageService {

	/**
	 * 메시지 발송
	 *
	 * @param messageParams
	 * */
	public MessageGateResDTO sendMessage(MessageGateSendReqDTO messageParams) throws Exception;

	/**
	 * 메시지 발송 상태 조회
	 *
	 * @param messageParams
	 * */
	public MessageGateStatusResDTO getStatus(MessageGateStatusReqDTO messageParams) throws Exception;

	/**
	 * 메시지 발송 내역 조회
	 *
	 * @param messageParams
	 * @return List<MessageGateResDTO>
	 * */
	public List<MessageGateResDTO> getSendMessageHistory(MessageGateSendReqDTO messageParams) throws
		Exception;

}
