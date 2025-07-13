package com.bwc.common.constant;

public enum ValidCodeEnum {

	/* NOT_FOUND */
	NOT_FOUND_SENDGUID("NOT_FOUND_SENDGUID", "서비스의 발송 고유 번호가 없습니다"),
	NOT_FOUND_SENDTYPE("NOT_FOUND_SENDTYPE", "발송 유형이 없습니다"),
	NOT_FOUND_SENDMSG("NOT_FOUND_SENDTYPE", "발송 내용이 없습니다"),
	NOT_FOUND_RECEIVERPHONE("NOT_FOUND_RECEIVERPHONE", "수신번호가 없습니다"),
	NOT_FOUND_SENDERNUMBER("NOT_FOUND_SENDERNUMBER", "발신번호가 없습니다"),
	NOT_FOUND_SERVICECODE("NOT_FOUND_SERVICECODE", "서비스 코드가 없습니다"),
	NOT_FOUND_SENDDATE("NOT_FOUND_SENDDATE", "전송 일자가 없습니다"),

	/* VALID */
	VALID_SENDTYPE("VALID_SENDTYPE", "발송 유형은 [SMS, MMS, TALK]만 입력 가능 합니다"),
	VALID_NUMTYPE("VALID_NUMTYPE", "숫자 형태의 파라미터가 아닙니다."),
	VALID_FORMAT_DATE("VALID_FORMAT_DATE", "YYYYMMDD 형태의 숫자를 기입해주세요."),

	/* MIN LENGTH */
	MAX_LEN_10("MIN_LEN_10", "수신번호는 10자리 이상만 가능 합니다."),

	/* MAX LENGTH */
	MAX_LEN_PHONE_13("MAX_LEN_PHONE_13", "수신번호는 13자리 까지만 가능 합니다."),
	MAX_LEN_CODE_40("MAX_LEN_CODE_40", "해당 코드는 40자 까지만 가능 합니다."),
	MAX_LEN_MSG_45("MAX_LEN_MSG_45", "발송 메시지는 한글45자 까지만 가능 합니다."),
	MAX_LEN_ETC_256("MAX_LEN_ETC_256", "기타필드는 256자 까지만 가능 합니다.");

	private String code;
	private String message;

	ValidCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
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
	 *
	 * 메시지 반환
	 *
	 * @param errorCode
	 * @return
	 */
	public static String getMsg(String errorCode) {
		String msg = "";

		for (ValidCodeEnum codeInfo : values()) {
			if (errorCode.equals(codeInfo.getCode())) {
				msg = codeInfo.getMessage();
				break;
			}
		}

		return msg;
	}
}


