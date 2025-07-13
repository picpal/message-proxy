package com.bwc.common.model;

import com.bwc.common.constant.ErrorCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ErrorCodeModle.java
 * @Description : 오류 시 응답 값
 * @author bwc205
 * @since 2021. 9. 3.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2021. 9. 3.     bwc205     	최초 생성
 * </pre>
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ErrorCodeModel {

	private String resultCode;

	private String resultMessage;

	@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
	private String pgRspnCode;

	@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
	private String pgRspnMessage;

	@Builder
	public ErrorCodeModel(String resultCode, String resultMessage, String pgRspnCode, String pgRspnMessage) {
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.pgRspnCode = pgRspnCode;
		this.pgRspnMessage = pgRspnMessage;
	}

	public static ErrorCodeModel createErrorCodeModel(ErrorCodeEnum errorCodeEnum) {
		return ErrorCodeModel.builder()
			.resultCode(errorCodeEnum.getCode())
			.resultMessage(errorCodeEnum.getMessage())
			.build();
	}

	public ErrorCodeModel(ErrorCodeEnum code) {
		this.resultCode = code.getCode();
		this.resultMessage = code.getMessage();
	}

	public void of(ErrorCodeEnum code) {
		this.resultCode = code.getCode();
		this.resultMessage = code.getMessage();
	}

}
