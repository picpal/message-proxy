package com.bwc.message.v1.sender.strategy.impl;

import java.util.List;

import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;

import com.bwc.common.constant.CommonCode;
import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.exception.MessageGateException;
import com.bwc.message.v1.gate.dao.MaMessageDAO;
import com.bwc.message.v1.gate.dto.MessageGateResDTO;
import com.bwc.message.v1.gate.dto.MessageGateSendReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;
import com.bwc.message.v1.sender.dao.lgu.v2.LguV2MessageDAO;
import com.bwc.message.v1.sender.jobs.ScheduleService;
import com.bwc.message.v1.sender.strategy.MessageServiceStrategy;
import com.bwc.message.v1.sender.strategy.MessageServiceType;
import com.bwc.message.v1.sender.vo.MessageStatusVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@MessageServiceType("lguV2")
public class LguV2MessageServiceStrategy implements MessageServiceStrategy {

	private final LguV2MessageDAO lguV2MessageDAO;
	private final MaMessageDAO maMessageDAO;
	private final ScheduleService scheduleService;

	public LguV2MessageServiceStrategy(LguV2MessageDAO lguV2MessageDAO, MaMessageDAO maMessageDAO,
		ScheduleService scheduleService) {
		this.lguV2MessageDAO = lguV2MessageDAO;
		this.maMessageDAO = maMessageDAO;
		this.scheduleService = scheduleService;
	}

	@Override
	public MessageGateResDTO sendMessage(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception {
		log.info("=================== [LGU V2] sendMessage ===================");

		// ### 메시지 발송 요청 (LGU v2는 SMS,MMS 통합 테이블 사용)
		lguV2MessageDAO.insertSendSmsMessage(messageGateSendReqDTO); // VENDER
		maMessageDAO.insertSendMessageOrigin(messageGateSendReqDTO); // MADBT

		// ### 메시지 상태 확인
		JobDataMap jobDataMap = messageGateSendReqDTO.getSendMessageInfo(); // scheduler 전달 파라미터
		jobDataMap.put("senderMessageDAO", lguV2MessageDAO);
		String jobName = scheduleService.getJobName("lguV2");
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

		// LGU+ V2는 통합테이블 형태
		MessageStatusVO result = lguV2MessageDAO.selectSmsMessageStatus(messageStatusVO);

		if (result == null) {
			throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_VENDER_STATUSDATA);
		}

		log.info("####### Message Gate 발송 상태 동기화");
		maMessageDAO.updateSendStatus(result);

		return result.responseStatus();
	}

	public List<MessageGateResDTO> getSendHistory(MessageGateSendReqDTO messageGateSendReqDTO) throws Exception {
		return null;
	}
}
