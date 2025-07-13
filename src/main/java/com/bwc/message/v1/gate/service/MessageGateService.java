package com.bwc.message.v1.gate.service;

import java.util.List;

import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;

import jakarta.servlet.http.HttpSession;

public interface MessageGateService {

	public MessageGateResDTO requestSendMessage(MessageGateSendReqDTO messageGateSendReqDTO, HttpSession session) throws
		Exception;

	public MessageGateStatusResDTO getStatus(MessageGateStatusReqDTO messageGateStatusReqDTO,
		HttpSession session) throws
		Exception;

	public List<MessageGateResDTO> getSendHistory(MessageGateSendReqDTO messageGateSendReqDTO,
		HttpSession session) throws
		Exception;

}
