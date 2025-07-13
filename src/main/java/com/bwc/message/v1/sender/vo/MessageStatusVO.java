package com.bwc.message.v1.sender.vo;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;

import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.exception.MessageGateException;
import com.bwc.common.util.Converter;
import com.bwc.common.util.DateUtil;
import com.bwc.common.util.StrUtil;
import com.bwc.message.v1.gate.dto.MessageGateStatusReqDTO;
import com.bwc.message.v1.gate.dto.MessageGateStatusResDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageStatusVO {

	private String sendUid;

	private String sessGuid;

	private String table;

	private String sendDate;

	private String serviceCode;

	private String status;

	private String sendType;

	private String vender;

	private String reslutCode;

	private String resultMsg;

	/**
	 * VO에 로직을 넣어서 찾기가 힘듬...
	 * 우선은 넣어두고 나중에 로직 분리 필요...
	 * 안 넣으면 밖에서 VO에 set으로 할당해야해서, set으로 할당 조차 막기 위해
	 * 내부 private으로 테이블 명을 설정.
	 *
	 *
	 * */
	private String getTableName(String vender, String sendType) {
		Map<String, String> tablePreFix = new HashMap<>();
		if ("SMS".equals(sendType.toUpperCase())) {
			tablePreFix.put("lguV1", "SC_LOG_");
			tablePreFix.put("lguV2", "UMS_LOG_");
		}
		if ("MMS".equals(sendType.toUpperCase())) {
			tablePreFix.put("lguV1", "MMS_LOG_");
			tablePreFix.put("lguV2", "UMS_LOG_");
		}

		String prefix = tablePreFix.get(vender);
		String month = DateUtil.getYyyymm();

		String result = prefix == null ? "NONE" : prefix + month;

		if (!"mts".equals(vender) && "NONE".equals(result)) {
			throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_TABLENAME);
		}
		return result;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MessageStatusVO setMessageStatusInfo(JobDataMap triggerDataMap) {

		return this.builder()
			.sendUid(triggerDataMap.getString("sendUid"))
			.sessGuid(triggerDataMap.getString("sessGuid"))
			.vender(triggerDataMap.getString("vender"))
			.serviceCode(triggerDataMap.getString("serviceCode"))
			.sendDate(DateUtil.getYyyymmdd())
			.sendType(triggerDataMap.getString("sendType"))
			.table(getTableName(triggerDataMap.getString("vender"), triggerDataMap.getString("sendType")))
			.build();
	}

	public MessageStatusVO setMessageStatusInfo(MessageGateStatusReqDTO messageGateStatusReqDTO) {
		return this.builder()
			.sendUid(messageGateStatusReqDTO.getSendUid())
			.sessGuid(messageGateStatusReqDTO.getSessGuid())
			.vender(messageGateStatusReqDTO.getVender())
			.serviceCode(messageGateStatusReqDTO.getServiceCode())
			.sendDate(messageGateStatusReqDTO.getSendDate())
			.sendType(messageGateStatusReqDTO.getSendType())
			.table(getTableName(messageGateStatusReqDTO.getVender(), messageGateStatusReqDTO.getSendType()))
			.build();
	}

	public MessageGateStatusResDTO responseStatus() {
		return MessageGateStatusResDTO.builder()
			.sendUid(StrUtil.nullToStr(this.getSendUid()))
			.sessGuid(StrUtil.nullToStr(this.getSessGuid()))
			.vender(StrUtil.nullToStr(this.getVender()))
			.status(StrUtil.nullToStr(Converter.convertMessageStatus(this.vender, this.getStatus(), this.reslutCode)))
			.serviceCode(StrUtil.nullToStr(this.getServiceCode()))
			.sendDate(StrUtil.nullToStr(this.getSendDate()))
			.sendType(StrUtil.nullToStr(this.getSendType()))
			.resultCode(StrUtil.nullToStr(this.getReslutCode()))
			.resultMsg(StrUtil.nullToStr(this.getResultMsg()))
			.build();
	}

}
