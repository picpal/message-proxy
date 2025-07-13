package com.bwc.common.util;

import org.apache.log4j.MDC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LogUtil {
	/**
	 *
	 * 로그 파라미터 초기화.
	 *
	 */
	public static void clear() {
		MDC.put("sessGuid", "");
	}

	/**
	 *
	 * 세션 ID 할당
	 *
	 */
	public static void setSessGuid(String sessGuid) {
		MDC.put("sessGuid", sessGuid);
	}

}
