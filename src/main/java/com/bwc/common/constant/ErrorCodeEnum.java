package com.bwc.common.constant;

import org.springframework.http.HttpStatus;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ErrorCodeEnum.java
 * @Description : 클래스 설명을 기술합니다.
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

public enum ErrorCodeEnum {
	SUCCESS("0000", "정상", HttpStatus.OK),

	VALID_PARAM("E100", "필수값 입니다.", HttpStatus.BAD_REQUEST),
	VALID_NUMBER("E101", "숫자 형식만 허용합니다.", HttpStatus.BAD_REQUEST),
	VALID_DATE("E102", "날짜 형식만 허용합니다.", HttpStatus.BAD_REQUEST),
	BAD_REQUEST("E105", "요청값을 확인해주세요.", HttpStatus.BAD_REQUEST),
	MAX_LEN_MSG("E106", "허용 가능한 최대 문자열 수를 초과 하였습니다.", HttpStatus.BAD_REQUEST),
	SORT_VALID_DATE("E109", "ASC, DESC만 입력이 가능합니다.", HttpStatus.BAD_REQUEST),
	DATE_TIME_VALID("E110", "최근 5년 초과 데이터는 조회 할 수 없습니다.", HttpStatus.BAD_REQUEST),
	DATE_90_VALID("E111", "최대 조회 기간은 90일 까지 입니다.", HttpStatus.BAD_REQUEST),
	END_DATE_LARGE("E112", "시작일자가 종료일자보다 최근일 수 없습니다.", HttpStatus.BAD_REQUEST),
	NOT_VALID_METHOD("E113", "GET, POST만 가능합니다.", HttpStatus.BAD_REQUEST),

	UNAUTHORIZED("E200", "인증되지 않았습니다.", HttpStatus.UNAUTHORIZED),

	DATA_FORMAT_ERROR("E317", "데이터 전송시 에러가 발생하였습니다.", HttpStatus.FORBIDDEN),
	NOT_FOUND_SENDTYPE("E401", "전송구분[sendType]이 없습니다.", HttpStatus.NOT_FOUND),
	NOT_FOUND_VENDER_STATUSDATA("E406", "발송 업체의 메시지 상태가 없습니다.", HttpStatus.NOT_FOUND),
	NOT_FOUND_TABLENAME("E402", "유효한 테이블명이 아닙니다", HttpStatus.NOT_FOUND),
	NOT_FOUND("E403", "존재하지 않는 url 입니다.", HttpStatus.NOT_FOUND),
	NOT_FOUND_JOB("E404", "메시지 업체에 해당하는 jobName이 없습니다.", HttpStatus.FORBIDDEN),
	NOT_FOUND_STRATEGY("E405", "메시지 업체의 객체를 찾을 수 없습니다", HttpStatus.FORBIDDEN),

	REQUEST_FAIL("E500", "허용되지 않은 HTTP Method 또는 잘못된 파라미터 값이 들어왔습니다.", HttpStatus.METHOD_NOT_ALLOWED),
	NOT_ALLOWED("E500", "허용되지 않은 Http Method 입니다.", HttpStatus.METHOD_NOT_ALLOWED),
	INTERSERVER_ERROR("E501", "서버에서 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	DISABILITY_NOTICE_ERROR("E502", "시스템 점검중 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private String code;
	private String message;
	private HttpStatus httpSatus;

	ErrorCodeEnum(String code, String message, HttpStatus httpSatus) {
		this.code = code;
		this.message = message;
		this.httpSatus = httpSatus;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the httpSatus
	 */
	public HttpStatus getHttpSatus() {
		return httpSatus;
	}

	/**
	 * @param httpSatus the httpSatus to set
	 */
	public void setHttpSatus(HttpStatus httpSatus) {
		this.httpSatus = httpSatus;
	}

	/**
	 *
	 * CriticalError 찾기
	 *
	 * @param errorCode
	 * @return
	 */
	public static boolean isCriticalError(String errorCode) {
		boolean result = false;

		if (errorCode.equals(INTERSERVER_ERROR.getCode())) {
			result = true;
		}
		return result;
	}

	/**
	 *
	 * 에러 메시지 반환
	 *
	 * @param errorCode
	 * @return
	 */
	public static String getMsg(String errorCode) {
		String msg = "";

		for (ErrorCodeEnum codeInfo : values()) {
			if (errorCode.equals(codeInfo.getCode())) {
				msg = codeInfo.getMessage();
				break;
			}
		}

		return msg;
	}

}








