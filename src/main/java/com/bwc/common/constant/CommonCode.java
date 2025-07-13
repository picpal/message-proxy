package com.bwc.common.constant;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : CommonCode.java
 * @Description : 공통 코드
 * @author 451773
 * @since 2023. 11. 23
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2023.11. 23.      451773     	    최초 생성
 * </pre>
 */

public enum CommonCode {

	SUCCESS("0000", "정상"),
	SUCCESS_SMS_SEND("0001", "메세지 전송 성공"),

	/* 1xxx : */
	NULL_ERROR("1000", "처리중 NULL 에러가 발생하였습니다.<br>지속적으로 발생시 고객센터에 문의 바랍니다."),

	/* 2xxx : */
	DB_ERROR("2000", "DB처리 중 오류가 발생하였습니다."),
	DB_UPDATE_LAST_LOGIN_ERROR("2002", "최종로그인일시 업데이트중 오류가 발생하였습니다."),
	DB_UPDATE_CNT0_ERROR("2005", "업데이트 대상이 없습니다."),
	DB_SELECT_SHT_URL_CNT0_ERROR("2012", "유효한 URL이 아닙니다.<br><br> SMS 발송을 다시 요청 바랍니다."),

	/* 3xxx : */

	/* 4xxx : */
	SYSTEM_ERROR("4000", "시스템오류가 발생하였습니다."),

	/* 5xxx : */

	/* 6xxx : */
	VALIDATE_ERROR("6000", "유효성오류가 발생하였습니다."),
	VALIDATE_LOGIN_ERROR("6010", "아이디 또는 패스워드가 맞지 않습니다.<br>확인 후 입력해주세요."),
	VALIDATE_LOGIN_STATUS_LOCK("6011", "아이디가 잠김상태입니다. 고객센터에 문의 바랍니다."),
	VALIDATE_URL_VHCLMSTBNO_ERROR("6021", "유효한 URL이나 차량원장번호가 없습니다. 고객센터에 문의 바랍니다."),

	/*7xxx : */
	INTERNAL_API_ERROR("7000", "내부연동 중 오류가 발생하였습니다."),
	INTERNAL_API_SMS_SEND_ERROR("7001", "SMS발송 중 오류가 발생하였습니다."),
	INTERNAL_API_CAR_INFO_CONTRACT_ERROR("7002", "차량정보 조회 중 오류가 발생하였습니다."),

	/* 8xxx : */
	EXTERNAL_API_ERROR("8000", "외부연동 중 오류가 발생하였습니다."),

	/* 9xxx : */
	ETC_ERROR("9000", "정의되지 않은 오류가 발생하였습니다.");

	private String code;
	private String msg;

	private CommonCode() {
	}

	private CommonCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
