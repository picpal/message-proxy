package com.bwc.message.v1.gate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageGateApiKeyResDTO {

	private String apiKey; /*Service API KEY*/
	private String salt; /*Service API KEY SALT*/
	private String serviceCode; /*Service Division Code*/

	private String resultCode;
	private String resultMsg;

}
