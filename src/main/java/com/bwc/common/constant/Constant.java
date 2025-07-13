package com.bwc.common.constant;

import java.util.regex.Pattern;

/**
 * SqlInjection과 공통 OK, Fail코드 기술
 *
 * Constants에 대한 설명 작성
 * @ClassName Constants.java
 * @Description Constants Class
 * @Modification-Information
 * <pre>
 *    수정일       수정자              수정내용
 *  ----------   ----------   -------------------------------
 *  2020. 6. 2.    lucifer      최초생성
 * </pre>
 * @author lucifer
 * @since 2020. 6. 2.
 * @version 1.0
 * @see
 * <pre>
 *  Copyright (C) 2020 by Taihoinst CO.,LTD. All right reserved.
 * </pre>
 */
public class Constant {

	public final static String OK_CODE = "0000";
	public final static String FAIL_CODE = "9999";

	public final static Pattern[] PATTERN = new Pattern[] {
		// Script fragments
		Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
		// src='...'
		//Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
		//Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
		// lonely script tags
		Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
		Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
		// eval(...)
		Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
		// expression(...)
		Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
		// javascript:...
		Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
		// vbscript:...
		Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
		// onload(...)=...
		Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
	};

	// RestTemplate http connection settting
	// 서버에 소켓 연결을 맺을 때의 타임아웃
	public final static int REST_CONNECT_TIMEOUT = 20;
	// 커넥션풀로부터 꺼내올때의 타임아웃
	public final static int REST_CONNECTION_REQUEST_TIMEOUT = 20;
	// 요청, 응답간의 타임아웃
	public final static int REST_SOCKET_TIMEOUT = 20;
	// 최대 커넥션 갯수
	public final static int REST_MAX_CONN_TOTAL = 50;
	// ip, domain name당 최대 커넥션 갯수
	public final static int REST_MAX_CONN_PER_ROUTE = 20;
	// Client측 idle Connection을 주기적으로 삭제
	public final static long EVICT_IDLE_CONNECTIONS = 60L;
	// 요청, 응답간의 타임아웃
	public final static int REST_READ_TIMEOUT = 30000;
	// Connection Thread Count
	public final static int REST_THREAD_COUNT = 20;

}
