package com.bwc.message.v1.gate.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.bwc.common.vo.HMap;
import com.bwc.message.v1.gate.dto.MessageGateApiKeyResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;
import com.bwc.message.v1.sender.vo.MessageStatusVO;

@Mapper
public interface MaMessageDAO {
	public String selectMessageMapping(String serviceCode) throws Exception;

	public MessageGateStatusResDTO selectSendMessageInfo(MessageGateStatusReqDTO messageGateStatusReqDTO) throws
		Exception;

	public HMap selectMessageLog(MessageStatusVO messageStatusVO) throws Exception;

	public MessageGateApiKeyResDTO selectApiKey(String serviceCode) throws Exception;

	public MessageGateApiKeyResDTO selectPreApiKey(String serviceCode) throws Exception;

	public void insertSendMessageOrigin(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception;

	public void updateCheckCount(MessageStatusVO messageStatusVO) throws Exception;

	public void updateSendStatus(MessageStatusVO messageStatusVO) throws Exception;

	// @TODO 백오피스 기능으로 빼야함.
	public void insertReHstServiceInfo(Map params) throws Exception;

	// @TODO 백오피스 기능으로 빼야함.
	public void insertServiceInfo(Map params) throws Exception;

	// @TODO 백오피스 기능으로 빼야함.
	public void removeServiceInfo(Map params) throws Exception;

}



