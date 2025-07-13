package com.bwc.message.v1.gate.dto;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;

import com.bwc.common.util.DateUtil;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageGateSendReqDTO {
	private String sessGuid; // 트랜젝션 추적

	@NotEmpty(message = "NOT_FOUND_SENDGUID")
	private String sendUid; // 전송 고유번호 (고유번호 생성)

	@NotEmpty(message = "NOT_FOUND_SENDTYPE")
	@Pattern(regexp = "^(SMS|MMS|TALK)$", message = "VALID_SENDTYPE")
	private String sendType; // 발송유형 (SMS, MMS, TALK)

	private String sendDate; // 발송 시간 ( 미래 시간을 적으면 예약 발송 )

	@NotEmpty(message = "NOT_FOUND_SENDMSG")
	private String sendMsg; // 발송 메시지 ( SMS : 150byte / MMS : 2,000 byte / TALK : 2,000 byte )

	private String status; // 발송 상태

	@NotEmpty(message = "NOT_FOUND_RECEIVERPHONE")
	@Pattern(regexp = "^[0-9]*$", message = "VALID_NUMTYPE")
	@Size(min = 10, max = 13)
	private String receiverPhone; // 수신번호

	@NotEmpty(message = "NOT_FOUND_SENDERNUMBER")
	@Size(min = 8, max = 9)
	private String senderNumber; // 회신 번호

	@NotEmpty(message = "NOT_FOUND_SERVICECODE")
	@Size(min = 1, max = 40, message = "MAX_LEN_CODE_40")
	private String serviceCode; // 요청 서비스 코드

	private String vender; // message 서비스 업체

	@Size(max = 40, message = "MAX_LEN_CODE_40")
	private String templateCode; // 메시지 템플릿 코드

	private String resendCount; // 재발송 횟수

	private String statusChkCnt; // 상태 체크 횟수

	@Size(max = 256, message = "MAX_LEN_ETC_256")
	private String etc1; // 예비 필드1

	@Size(max = 256, message = "MAX_LEN_ETC_256")
	private String etc2; // 예비 필드2

	@Size(max = 256, message = "MAX_LEN_ETC_256")
	private String etc3; // 예비 필드3

	@Size(max = 45, message = "MAX_LEN_MSG_45")
	private String subject;

	private String jsonParam1;

	private String jsonParam2;

	// 발송 상태 [MTS]
	// - 1:전송
	// - 2:mts전송완료
	// - 3:수신완료
	// - 5:senderror
	// - 6:내부버퍼삽입완료

	// 발송 상태 [LGU v1]
	// - 0 : 발송대기
	// - 1 : 결과수신대기
	// - 2 : 결과수신완료

	// 발송 상태 [LGU v2]
	// - ready : 발송 대기(insert 상태 값)
	// - request : 발송 완료
	// - complete : 결과 수신 완료
	// - pre-send : select 완료
	// - pre-image : 이미지 등록 작업 중

	public void setVender(String vender) {
		this.vender = vender;
	}

	public void setSessGuid(String sessGuid) {
		this.sessGuid = sessGuid;
	}

	public JobDataMap getSendMessageInfo() {
		JobDataMap result = new JobDataMap();
		result.put("sessGuid", this.sessGuid);
		result.put("sendUid", this.sendUid);
		result.put("vender", this.vender);
		result.put("serviceCode", this.serviceCode);
		result.put("sendType", this.sendType);
		result.put("sendDate", DateUtil.getYyyymmdd());

		return result;
	}

	public Map<String, String> getStatusParams() {
		Map<String, String> result = new HashMap<>();
		result.put("sessGuid", this.sessGuid);
		result.put("sendUid", this.sendUid);
		result.put("vender", this.vender);
		result.put("serviceCode", this.serviceCode);
		result.put("sendType", this.sendType);
		result.put("sendDate", this.sendDate);

		return result;
	}
}
