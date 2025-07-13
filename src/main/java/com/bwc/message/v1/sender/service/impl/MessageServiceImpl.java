package com.bwc.message.v1.sender.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.exception.MessageGateException;
import com.bwc.common.util.StrUtil;
import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;
import com.bwc.message.v1.sender.service.MessageService;
import com.bwc.message.v1.sender.strategy.MessageServiceStrategy;
import com.bwc.message.v1.sender.strategy.StrategyFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

	private final StrategyFactory strategyFactory;

	@Autowired
	public MessageServiceImpl(StrategyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}

	@Override
	public MessageGateResDTO sendMessage(MessageGateSendReqDTO messageParams) throws Exception {
		boolean isValid = validSendMsgParams(messageParams);
		if (!isValid) {
			throw new MessageGateException(ErrorCodeEnum.MAX_LEN_MSG);
		}
		MessageServiceStrategy strategy = getStrategy(messageParams.getVender());
		return strategy.sendMessage(messageParams);
	}

	@Override
	public MessageGateStatusResDTO getStatus(MessageGateStatusReqDTO messageParams) throws Exception {
		MessageServiceStrategy strategy = getStrategy(messageParams.getVender());
		return strategy.getStatusForVender(messageParams);
	}

	@Override
	public List<MessageGateResDTO> getSendMessageHistory(MessageGateSendReqDTO messageParams) throws Exception {
		MessageServiceStrategy strategy = getStrategy(messageParams.getVender());
		return strategy.getSendHistory(messageParams);
	}

	/**
	 * 요청한 Vender사에 맞는 객체 조회
	 * @param messageVender
	 * */
	private MessageServiceStrategy getStrategy(String messageVender) {
		MessageServiceStrategy strategy = strategyFactory.getStrategy(messageVender);
		if (strategy != null) {
			return strategy;
		} else {
			log.error("####### Not Found Strategy , vender : [{}] ", messageVender);
			throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}
	}

	private boolean validSendMsgParams(MessageGateSendReqDTO messageParams) {

		if ("SMS".equals(messageParams.getSendType())) {
			if (StrUtil.getByteLength(messageParams.getSendMsg()) > 150) { // MAX 150 byte
				return false;
			}
		} else {
			if (StrUtil.getByteLength(messageParams.getSendMsg()) > 2000) { // MAX 2,000 byte
				return false;
			}
		}

		return true;
	}

}
