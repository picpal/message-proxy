package com.bwc.message.v1.sender.jobs;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.exception.MessageGateException;
import com.bwc.common.util.Converter;
import com.bwc.common.util.StrUtil;
import com.bwc.common.vo.HMap;
import com.bwc.message.v1.gate.dao.MaMessageDAO;
import com.bwc.message.v1.sender.dao.SenderMessageDAO;
import com.bwc.message.v1.sender.vo.MessageStatusVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleService {

	private final MaMessageDAO maMessageDAO;
	private final Scheduler scheduler;

	public ScheduleService(MaMessageDAO maMessageDAO, Scheduler scheduler) {
		this.maMessageDAO = maMessageDAO;
		this.scheduler = scheduler;
	}

	/**
	 * 메시지 발송 상태 체크 여부
	 *
	 * @param JobExecutionContext context
	 * @return boolean
	 *
	 * @description :
	 * Max 요청 최대 3회 ( 2분 * 3회 )
	 * Max 제한이 없는 경우 : 통신사 오류로 발송 대기 시 모든 요청이 재 발송 되어 요청에 의한 트래픽이 증가됨.
	 */
	private boolean isReCheck(JobExecutionContext context) {
		try {
			JobDataMap triggerDataMap = context.getTrigger().getJobDataMap();

			// 메시지 파라미터 유효성 체크
			boolean isValid = isValid(triggerDataMap);
			if (!isValid) {
				log.error("Parameter Error jobDataMap : {}", StrUtil.convertMapToJsonStr((Map)triggerDataMap));
				throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
			}

			// 메시지 파라미터 설정
			MessageStatusVO messageStatusVO = new MessageStatusVO().setMessageStatusInfo(triggerDataMap);

			// Agent의 메시지 발송 상태 조회
			SenderMessageDAO senderMessageDAO = (SenderMessageDAO)triggerDataMap.get("senderMessageDAO");
			MessageStatusVO venderMessageStatus = getVenderMessageStatus(senderMessageDAO, messageStatusVO);

			// 발송 상태 공통 코드로 변환
			String status = Converter.convertMessageStatus(
				messageStatusVO.getVender(),
				venderMessageStatus.getStatus(),
				venderMessageStatus.getReslutCode());

			// 메시지 발송 상태 확인 횟수
			int checkCount = getReCheckCount(messageStatusVO);

			// 발송대기 && 발송 상태 확인 횟수가 4회 미만
			if ("01".equals(status) && checkCount < 4) {
				if (checkCount > 2) {
					checkVenderStatus(context);
				}
				return true;
			}

			// 발송 상태가 대기가 아닌 경우 발송 상태 변경
			if (!"01".equals(status)) {
				messageStatusVO.setStatus(status);
				maMessageDAO.updateSendStatus(messageStatusVO);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}

		return false;
	}

	/**
	 * 메시지 파라미터 유효성 검증
	 *
	 * @param JobExecutionContext context
	 * @return boolean
	 *
	 */
	private boolean isValid(JobDataMap triggerDataMap) {
		String[] nullCheckParams = {
			"sendUid"
			, "sessGuid"
			, "vender"
			, "serviceCode"
			, "sendDate"
			, "sendType"
			, "vender"
		};
		for (String nullCheckParam : nullCheckParams) {
			if (triggerDataMap.get(nullCheckParam) == null || "".equals(triggerDataMap.get(nullCheckParam))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 업체의 메시지 발송 상태 확인
	 *
	 * @param JobDataMap triggerDataMap
	 * @return String
	 *
	 * */
	private MessageStatusVO getVenderMessageStatus(SenderMessageDAO senderMessageDAO, MessageStatusVO messageStatusVO) {
		try {
			String sendType = messageStatusVO.getSendType().toUpperCase();
			String vender = messageStatusVO.getVender();

			// lgu v2는 통합 테이블 사용
			if ("lguV2".equals(vender)) {
				return senderMessageDAO.selectSmsMessageStatus(messageStatusVO);
			}

			// mts, lgu v1은 전송 유형별 테이블이 별도로 존재
			switch (sendType) {
				case "SMS":
					return senderMessageDAO.selectSmsMessageStatus(messageStatusVO);
				case "MMS":
					return senderMessageDAO.selectMmsMessageStatus(messageStatusVO);
				default:
					log.error("####### sendType : {}", sendType);
					throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_SENDTYPE);
			}
		} catch (Exception e) {
			throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}
	}

	/**
	 * 메시지 발송 상태 확인 횟수 체크
	 *
	 * @param JobDataMap triggerDataMap
	 * @return int
	 *
	 * */
	private int getReCheckCount(MessageStatusVO messageStatusVO) throws Exception {
		maMessageDAO.updateCheckCount(messageStatusVO);
		HMap messageLogData = maMessageDAO.selectMessageLog(messageStatusVO);

		if (messageLogData == null || messageLogData.get("resendCount") == null) {
			log.error("Parameter Error : {}", StrUtil.convertMapToJsonStr((Map)messageLogData));
			throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}

		BigDecimal bigDecimalValue = new BigDecimal(String.valueOf(messageLogData.get("resendCount")));
		return bigDecimalValue.intValue();
	}

	/**
	 * 업체 메시지 발송 Health check
	 *
	 * @param JobExecutionContext context
	 *
	 * */
	private void checkVenderStatus(JobExecutionContext context) {
		JobDataMap triggerDataMap = context.getTrigger().getJobDataMap();
		try {
			// 현재 건의 발송 정보로 업체 전체 발송 상태 확인 횟수가 3회 이상인 건수 조회
			MessageStatusVO messageStatusVO = new MessageStatusVO();
			messageStatusVO = messageStatusVO.setMessageStatusInfo(triggerDataMap);
			HMap messageLog = maMessageDAO.selectMessageLog(messageStatusVO);

			// 3회 이상 발송 상태가 변경 되지 않은 건수가 [N]건 이상일 때 
			BigDecimal bigDecimalValue = new BigDecimal(String.valueOf(messageLog.get("resendCount")));
			int resendCount = bigDecimalValue.intValue();

			// @TODO 10건 지정 ( 백오피스에서 설정 가능 하도록 기능 추가 )
			if (resendCount > 10) {
				// A. [시스템 Self 복원] default -> backup Message로 변경 후 발송
				//   - 네이버웍스 또는 관리자에게 Noti 발송
				//   - 3회 발송 처리 안된 건이 전체 발송 내역에서 10건 이상인 경우.
				//   - 같은 업체를 사용하는 서비스 모두 조회하여 backup message로 변경
				String serviceCode = messageLog.getString("serviceCode");
				changeMessageVender(serviceCode, resendCount);

				// B. [수동 복원] Noti 발송 ( 성혁 시니어님이 개발한 모니터링으로 처리 가능 )
				//   - 관리자가 직접 데이터 확인 후 default -> backup Message로 변경
				//   - DB or API or 백오피스(일정 미정)
			}
		} catch (Exception e) {
			throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
		}
	}

	/**
	 * 서비스에 맵핑된 기본 vender의 오류로 backup vender 업체로 변경
	 *
	 * @param String status
	 * */
	private void changeMessageVender(String serviceCode, long resendCount) {
		log.info("====================== changeMessageVender ============================");

	}

	/**
	 * 업체의 전체 발송 상태 확인
	 *
	 * @param JobExecutionContext context
	 * */
	public void triggerJobSchedule(String jobName, JobDataMap jobDataMap) throws
		SchedulerException {
		JobKey jobKey = new JobKey(jobName);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);

		if (jobDetail == null) {
			log.error("JobDetail not found for : {}", jobKey);
			throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_JOB);
		}

		long delay = 2 * 60 * 1000; // 스케줄 간격 설정 (@TODO properties or DB로 관리)
		SimpleTrigger trigger = TriggerBuilder.newTrigger()
			.forJob(jobDetail)
			.usingJobData(jobDataMap)
			.startAt(new Date(System.currentTimeMillis() + delay))
			.withSchedule(SimpleScheduleBuilder.simpleSchedule())
			.build();

		scheduler.scheduleJob(trigger);
	}

	/**
	 * Job 실행
	 *
	 * @param JobExecutionContext context
	 * @param String vender
	 * */
	public void executeJob(JobExecutionContext context, String vender) {
		boolean isReCheck = isReCheck(context);
		if (isReCheck) {
			try {
				JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
				triggerJobSchedule(getJobName(vender), jobDataMap);
			} catch (SchedulerException e) {
				throw new MessageGateException(ErrorCodeEnum.INTERSERVER_ERROR);
			}
		}
	}

	/**
	 * vender에 따른 MessageJobName 조회 맵핑
	 * ( 이 부분은 상수로 빼자... )
	 * */
	public String getJobName(String vender) {
		switch (vender) {
			case "mts":
				return "MtsMessageSyncJob";
			case "lguV1":
				return "LguV1MessageSyncJob";
			case "lguV2":
				return "LguV2MessageSyncJob";
			default:
				throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_JOB);
		}
	}

}
