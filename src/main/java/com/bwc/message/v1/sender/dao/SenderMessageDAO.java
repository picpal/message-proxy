package com.bwc.message.v1.sender.dao;

import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.sender.vo.MessageStatusVO;

public interface SenderMessageDAO {
	public void insertSendSmsMessage(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception;

	public void insertSendMmsMessage(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception;

	public void insertSendLmsMessage(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception;

	public MessageStatusVO selectSmsMessageStatus(MessageStatusVO messageStatusVO) throws
		Exception;

	public MessageStatusVO selectMmsMessageStatus(MessageStatusVO messageStatusVO) throws
		Exception;
}
