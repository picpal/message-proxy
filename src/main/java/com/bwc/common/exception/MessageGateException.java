package com.bwc.common.exception;

import org.springframework.http.HttpStatus;

import com.bwc.common.constant.ErrorCodeEnum;

import lombok.AllArgsConstructor;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : baroPayException.java
 * @Description : 클래스 설명을 기술합니다.
 * @author bwc205
 * @since 2021. 9. 13.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2021. 9. 13.     bwc205         최초 생성
 * </pre>
 */
@AllArgsConstructor
public class MessageGateException extends RuntimeException {

	private String resultCode;
	private String resultMessage;
	private HttpStatus status;

	public MessageGateException(ErrorCodeEnum errorCode) {
		resultCode = errorCode.getCode();
		resultMessage = errorCode.getMessage();
		status = errorCode.getHttpSatus();
	}

	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @return the resultMessage
	 */
	public String getResultMessage() {
		return resultMessage;
	}

	/**
	 * @param resultMessage the resultMessage to set
	 */
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

}
