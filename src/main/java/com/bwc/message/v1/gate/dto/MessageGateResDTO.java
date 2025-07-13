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
public class MessageGateResDTO {

	private String resultCode;
	private String resultMsg;

}
