package com.bwc.message.v1.gate.dto;

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
public class MessageGateStatusResDTO {
	private String sessGuid; // 트랜젝션 추적

	private String sendUid; // 전송 고유번호 (고유번호 생성)

	private String sendDate; // 발송 일자

	private String status; // 발송 상태

	private String vender; // 발송 업체

	private String sendType; // 발송 구분

	private String serviceCode; // 요청 서비스 코드

	private String resultCode;

	private String resultMsg;
}
