package com.bwc.message.v1.sender.strategy.impl;

import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.springframework.stereotype.Component;

import com.bwc.common.constant.CommonCode;
import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.exception.MessageGateException;
import com.bwc.message.v1.gate.dao.MaMessageDAO;
import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;
import com.bwc.message.v1.sender.dao.mts.MtsMessageDAO;
import com.bwc.message.v1.sender.jobs.ScheduleService;
import com.bwc.message.v1.sender.strategy.MessageServiceStrategy;
import com.bwc.message.v1.sender.strategy.MessageServiceType;
import com.bwc.message.v1.sender.vo.MessageStatusVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@MessageServiceType("mts")
public class MtsMessageServiceStrategy implements MessageServiceStrategy {

	private final MtsMessageDAO mtsMessageDAO;
	private final MaMessageDAO maMessageDAO;
	private final ScheduleService scheduleService;

	public MtsMessageServiceStrategy(MtsMessageDAO mtsMessageDAO, MaMessageDAO maMessageDAO, Scheduler scheduler,
		ScheduleService scheduleService) {
		this.mtsMessageDAO = mtsMessageDAO;
		this.maMessageDAO = maMessageDAO;
		this.scheduleService = scheduleService;
	}

	@Override
	public MessageGateResDTO sendMessage(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception {
		log.info("=================== [MTS] sendMessage ===================");

		// ### 메시지 발송 요청 : VENDER
		String messageType = messageGateSendReqDTO.getSendType().toUpperCase();
		switch (messageType) {
			case "SMS":
				mtsMessageDAO.insertSendSmsMessage(messageGateSendReqDTO);
				break;
			case "MMS":
				mtsMessageDAO.insertSendMmsMessage(messageGateSendReqDTO);
				break;
			case "TALK":
				break;
			default:
				throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}
		maMessageDAO.insertSendMessageOrigin(messageGateSendReqDTO); // MADBT

		// ### 메시지 상태 확인
		JobDataMap jobDataMap = messageGateSendReqDTO.getSendMessageInfo(); // scheduler 전달 파라미터
		jobDataMap.put("senderMessageDAO", mtsMessageDAO);
		String jobName = scheduleService.getJobName("mts");
		scheduleService.triggerJobSchedule(jobName, jobDataMap);

		return MessageGateResDTO.builder()
			.resultCode(CommonCode.SUCCESS.getCode())
			.resultMsg(CommonCode.SUCCESS.getMsg())
			.build();
	}

	@Override
	public MessageGateStatusResDTO getStatusForVender(MessageGateStatusReqDTO messageGateStatusReqDTO) throws
		Exception {
		// validate
		String sendType = messageGateStatusReqDTO.getSendType();
		if (sendType == null || "".equals(sendType)) {
			throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_SENDTYPE);
		}

		MessageStatusVO messageStatusVO = new MessageStatusVO();
		messageStatusVO = messageStatusVO.setMessageStatusInfo(messageGateStatusReqDTO);

		MessageStatusVO result = new MessageStatusVO();
		switch (sendType.toUpperCase()) {
			case "SMS":
				result = mtsMessageDAO.selectSmsMessageStatus(messageStatusVO);
				break;
			case "MMS":
				result = mtsMessageDAO.selectMmsMessageStatus(messageStatusVO);
				break;
			default:
				throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}

		if (result == null) {
			throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_VENDER_STATUSDATA);
		}

		log.info("####### Message Gate 발송 상태 동기화");
		maMessageDAO.updateSendStatus(result);

		return result.responseStatus();
	}

	public List<MessageGateResDTO> getSendHistory(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception {
		List<MessageGateResDTO> sendHistory = null;
		return sendHistory;
	}
}


