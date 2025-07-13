package com.bwc.message.v1.sender.strategy;

import java.util.List;

import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;

public interface MessageServiceStrategy {

	/**
	 * 메시지 전송
	 *
	 * @param messageGateSendReqDTO
	 * */

	public MessageGateResDTO sendMessage(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception;

	/**
	 * 메시지 발송 상태 조회
	 *
	 * @param messageGateStatusReqDTO
	 * */

	public MessageGateStatusResDTO getStatusForVender(MessageGateStatusReqDTO messageGateStatusReqDTO) throws Exception;

	/**
	 * MTS 메시지 발송내역 조회
	 *
	 * @param messageGateSendReqDTO
	 * */
	public List<MessageGateResDTO> getSendHistory(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception;

}
