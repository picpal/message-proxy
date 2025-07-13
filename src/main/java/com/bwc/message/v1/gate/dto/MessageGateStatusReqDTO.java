package com.bwc.message.v1.gate.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageGateStatusReqDTO {
	private String sessGuid; // 트랜젝션 추적

	@NotEmpty(message = "NOT_FOUND_SENDGUID")
	private String sendUid; // 전송 고유번호 (고유번호 생성)

	@NotEmpty(message = "NOT_FOUND_SENDDATE")
	@Size(min = 8, max = 8, message = "VALID_FORMAT_DATE")
	@Pattern(regexp = "^[0-9]*$", message = "VALID_FORMAT_DATE")
	private String sendDate; // 발송 일자

	private String status; // 발송 상태

	private String vender; // 발송 업체

	private String sendType; // 발송 구분

	private String table; // 조회 테이블

	@NotEmpty(message = "NOT_FOUND_SERVICECODE")
	@Size(min = 1, max = 40, message = "MAX_LEN_CODE_40")
	private String serviceCode; // 요청 서비스 코드

	public void setVender(String vender) {
		this.vender = vender;
	}

	public void setSessGuid(String sessGuid) {
		this.sessGuid = sessGuid;
	}

	public MessageGateStatusReqDTO setStatusParams(MessageGateStatusResDTO messageGateStatusResDTO) {
		return MessageGateStatusReqDTO.builder()
			.serviceCode(messageGateStatusResDTO.getServiceCode())
			.sendUid(messageGateStatusResDTO.getSendUid())
			.sessGuid(messageGateStatusResDTO.getSessGuid())
			.sendDate(messageGateStatusResDTO.getSendDate())
			.sendType(messageGateStatusResDTO.getSendType())
			.status(messageGateStatusResDTO.getStatus())
			.vender(messageGateStatusResDTO.getVender())
			.build();
	}

}
