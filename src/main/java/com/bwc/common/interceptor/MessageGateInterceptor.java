package com.bwc.common.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bwc.common.util.NtnoCrtnUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class MessageGateInterceptor implements HandlerInterceptor {
	private final NtnoCrtnUtils ntnoCrtnUtils;

	public MessageGateInterceptor(NtnoCrtnUtils ntnoCrtnUtils) {
		this.ntnoCrtnUtils = ntnoCrtnUtils;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		HttpSession session = request.getSession(true); // session이 없는 경우 새로 생성

		// 요청 마다 신규 Session Unique ID 생성
		String sessGuid = ntnoCrtnUtils.occrSessionGuid();
		session.setAttribute("sessGuid", sessGuid);

		MDC.put("sessGuid", session.getAttribute("sessGuid").toString());

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		Exception ex) throws Exception {
		MDC.clear();
	}
}
