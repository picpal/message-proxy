package com.bwc.common.constant;

public enum SendStatus {
	/**
	 * LG U+ v2
	 * */
	LGU_V2_001("ready", "01", "[LG_V2] 발송 대기 상태"),
	LGU_V2_002("request", "02", "[LG_V2] 발송 완료 상태"),
	LGU_V2_003("complete", "03", "[LG_V2] 발송 결과 수신 완료 상태"),
	LGU_V2_005("error", "05", "[LG_V2] 발송 오류"),
	LGU_V2_006("READY", "06", "[LG_V2] 이미지 등록 작업 중"),
	LGU_V2_007("READY", "07", "[LG_V2] select 완료"),

	/**
	 * LG U+ v1
	 * */
	LGU_V1_001("0", "01", "[LG_V1] 발송 대기 상태"),
	LGU_V1_002("1", "02", "[LG_V1] 결과 수신 대기"),
	LGU_V1_003("2", "03", "[LG_V1] 결과 수신 완료"),
	LGU_V1_005("2", "05", "[LG_V1] 발송 오류"),

	/**
	 * MTS
	 * */
	MTS_001("1", "01", "[MTS] 전송 대기 상태 )"),
	MTS_002("2", "02", "[MTS] 전송 완료"),
	MTS_003("3", "03", "[MTS] Report 수신 완료"),
	MTS_005("5", "05", "[MTS] 발송 오류"),
	MTS_006("6", "06", "[MTS] 내부 버퍼 삽입 완료");

	private String originStatusCode;
	private String statusCode;
	private String statusDescript;

	SendStatus(String originStatusCode, String statusCode, String statusDescript) {
		this.originStatusCode = originStatusCode;
		this.statusCode = statusCode;
		this.statusDescript = statusDescript;
	}

	public String getOriginStatusCode() {
		return originStatusCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusDescript() {
		return statusDescript;
	}

	/**
	 *
	 * 코드에 대한 설명 반환
	 *
	 * @param originStatusCode
	 * @return
	 */
	public static String convertStatusCode(String originStatusCode) {
		for (SendStatus codeInfo : values()) {
			if (originStatusCode.equals(codeInfo.getOriginStatusCode())) {
				return codeInfo.getStatusCode();
			}
		}

		return "01";
	}

	/**
	 *
	 * 코드에 대한 설명 반환
	 *
	 * @param statusCode
	 * @return
	 */
	public static String getStatusDescription(String statusCode) {
		for (SendStatus codeInfo : values()) {
			if (statusCode.equals(codeInfo.getStatusCode())) {
				return codeInfo.getStatusDescript();
			}
		}

		return "";
	}

}
