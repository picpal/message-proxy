package com.bwc.common.aspect;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bwc.common.util.StrUtil;
import com.bwc.message.v1.gate.dao.MaMessageDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class MessageGateAspect {

	@Autowired
	protected MessageSource messageSource;

	private final MaMessageDAO maMessageDAO;

	public MessageGateAspect(MaMessageDAO maMessageDAO) {
		this.maMessageDAO = maMessageDAO;
	}

	@Pointcut("execution(* com.bwc.message.v1.gate.controller.*.*(..))")
	public void gateControllLayer() {
	}

	@Pointcut("execution(* com.bwc.message.v1.sender.jobs.*.*(..))")
	public void senderJobLayer() {
	}

	@Pointcut("execution(* com.bwc.message.v1.gate.dao.*.*(..))")
	public void gateQeuryLayer() {
	}

	@Pointcut("execution ( * com.bwc.common.controller.MessageGateExceptionHandler.*(..))")
	public void MessageGateExceptionLayer() {
	}

	/**
	 *
	 * JOB 실행 로그
	 *
	 * @param joinpoint
	 * @throws Exception
	 */
	@Before(value = "senderJobLayer()")
	public void preJobLog(JoinPoint joinpoint) throws Exception {
		methodPrint("START JOB LOGGING", joinpoint);
	}

	/**
	 *
	 * JOB 실행 완료 로그
	 *
	 * @param joinpoint
	 * @throws Exception
	 */
	@AfterReturning(value = "senderJobLayer()", returning = "returnValue")
	public void resultJobLog(JoinPoint joinpoint, Object returnValue) throws Exception {
		methodPrint("END JOB LOGGING", joinpoint);
	}

	/**
	 *
	 * 메시지 발송 Strategy 객체 실행 로그
	 * 적용 못함 : 전략패턴에 사용되는 MessageServiceType class에서 type객체에 null로 인해서 런타임 호출 오류가 나타남
	 * @param joinpoint
	 * @throws Exception
	 */
	// @Before(value = "senderStrategyLayer()")
	// public void strategyLog(JoinPoint joinpoint) throws Exception {
	// 	methodPrint("STRATEGY LOGGING", joinpoint);
	// 	printJoinPoint(joinpoint);
	// }

	/**
	 *
	 * Endpoint 호출시 로그 출력용
	 *
	 * @param joinpoint
	 * @throws Exception
	 */
	@Before(value = "gateControllLayer()")
	public void preLog(JoinPoint joinpoint) throws Exception {
		prePrint("", joinpoint);
	}

	/**
	 *
	 * 접속로그(사용자 행동 데이터) 저장 용
	 * Messeage 발송 테이블에 기록되어 우선은 보류
	 * 백오피스가 생기거나, 외부에서 MessageGate 서비스에 대한 옵션 변경이 필요한 경우 추가
	 *
	 * @param joinpoint
	 * @throws Exception
	 */
	// @Around(value = "gateQeuryLayer()")
	// public Object setAround(ProceedingJoinPoint joinpoint) throws Throwable {
	// 	log.info("####### START SAVE CNNC LOG !!!!!!!!!");
	// 	String type = joinpoint.getSignature().toShortString();
	// 	String methodName = joinpoint.getSignature().getName();
	// 	String className = joinpoint.getSignature().getDeclaringTypeName();
	//
	// 	HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
	// 	HttpSession session = request.getSession();
	// 	String sessGuid = StrUtil.nullToStr((String)session.getAttribute("SESS_GUID"));
	//
	// 	log.info("[{}] ####### type : {} , method : {} , className : {} ", sessGuid, type, methodName, className);
	//
	// 	return joinpoint.proceed();
	// }

	/**
	 *
	 * 정상적으로 응답 데이터가 갔을 떄, 로그 저장용
	 *
	 * @param joinpoint
	 * @param returnValue
	 * @throws Exception
	 */
	@AfterReturning(value = "gateControllLayer()", returning = "returnValue")
	public void resultLog(JoinPoint joinpoint, Object returnValue) throws Exception {
		resultPrint(joinpoint, returnValue);
	}

	/**
	 *
	 * 에러가 났을 떄, 로그 저장 용
	 *
	 * @param joinpoint
	 * @param returnValue
	 * @throws Exception
	 */
	@AfterReturning(pointcut = "MessageGateExceptionLayer()", returning = "returnValue")
	public void exceptionLog(JoinPoint joinpoint, Object returnValue) throws Exception {
		log.info(returnValue.toString());
		resultPrint(joinpoint, returnValue);
	}

	/**
	 *
	 * 실행 로그 출력
	 *
	 * @param prefix
	 * @param joinpoint
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void prePrint(String prefix, JoinPoint joinpoint) throws Exception {
		String sessGuid = MDC.get("sessGuid");
		String methodName = joinpoint.getSignature().getName();
		String className = joinpoint.getSignature().getDeclaringTypeName();
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String requestUri = request.getRequestURI();
		log.info(
			"[{}] =================== [ START REQUEST ( {} ) ] ClassName : {} , MethodName :  {} ===================",
			sessGuid,
			requestUri,
			methodName,
			className);
	}

	/**
	 *
	 * 응답 로그 출력
	 *
	 * @param resultPrint
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void resultPrint(JoinPoint joinpoint, Object resultPrint) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String sessGuid = MDC.get("sessGuid");
		String requestUri = request.getRequestURI();
		String methodName = joinpoint.getSignature().getName();
		String className = joinpoint.getSignature().getDeclaringTypeName();
		log.info(
			"[{}] =================== [ END RESPONSE ( {} ) ] ClassName : {} , MethodName :  {} ===================",
			sessGuid,
			requestUri,
			methodName,
			className);

		// Object의 데이터 형태 확인
		boolean isValid = resultPrint == null
			? false
			: !"String".equals(resultPrint.getClass().getSimpleName());
		if (isValid) {
			Map<String, Object> BaseMap = mapper.readValue(mapper.writeValueAsString(resultPrint), HashMap.class);
			String responseData = StrUtil.convertMapToJsonStr((Map)BaseMap.get("body"));
			log.info("[{}] ####### END RESPONSE PARAMS :  {} ", sessGuid, cutLogText(responseData));
			log.info("==========================================================");
		}
	}

	private void methodPrint(String prefix, JoinPoint joinpoint) throws Exception {
		String sessGuid = MDC.get("sessGuid");
		Signature signature = joinpoint.getSignature();
		String methodName = signature.getName();
		log.info(
			"[{}] =================== {} [  Signature : {} , Method : {} ] =====================",
			StrUtil.nullToStr(sessGuid),
			prefix,
			signature.toString(),
			methodName
		);
	}

	private String cutLogText(String logTxt) {
		int logMaxLen = 500;
		return logTxt.length() >= logMaxLen ? logTxt.substring(0, logMaxLen) + "..." : logTxt;
	}

	/**
	 *
	 * 로그 저장
	 *
	 * @param joinpoint
	 * @param rtnVal
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void saveLog(JoinPoint joinpoint, Object rtnVal) throws Exception {
		// maMessageDAO.updateSendStatus();

		// 	try {
		// 		ObjectMapper mapper = new ObjectMapper();
		// 		Map<String, Object> BaseMap = new HashMap<>();
		//
		// 		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		// 		String requestUri = request.getRequestURI();
		//
		// 		BaseMap = mapper.readValue(mapper.writeValueAsString(rtnVal), HashMap.class);
		// 		Map map = (Map)BaseMap.get("body");
		// 		JSONObject json = new JSONObject();
		// 		String responseData = json.toJSONString();
		//
		// 		if (!requestUri.startsWith("/error") && !requestUri.equals("/api/health-check")) {
		// 			log.info("====================[[[[[  save log   ]]]]]]]=====================");
		// 			// CustomAuthBasic authBasic;
		// 			// String ssid = null;
		// 			// String affcId = null;
		// 			//
		// 			// Object prinicipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// 			// authBasic = (CustomAuthBasic)prinicipal;
		// 			// ssid = authBasic.getAuthUser().getSsid();
		// 			// affcId = authBasic.getUsername();
		// 			//
		// 			// String resultcode = json.get("resultCode").toString();
		// 			// String resultMsg = json.get("resultMessage").toString();
		// 			//
		// 			// HMap LogMap = new HMap();
		// 			// LogMap.put("sessGuid", StringUtils.isNotEmpty(ssid) ? ssid : ntnoCrtnUtils.occrSessionGuid());
		// 			// LogMap.put("cnncUrl", requestUri);
		// 			// LogMap.put("execNm", ClientUtils.changeExecNm(requestUri, joinpoint.getSignature().getName()));
		// 			// LogMap.put("cnncNm", getExecName(LogMap.getString("execNm")));
		// 			// LogMap.put("rsltCd", resultcode); // 결과코드
		// 			// LogMap.put("rsltMsg", resultMsg); // 결과메세지
		// 			// LogMap.put("type", joinpoint.getSignature().getDeclaringTypeName());
		// 			// LogMap.put("method", joinpoint.getSignature().getName());
		// 			//
		// 			// commonService.insertTbAffcCnncLog(LogMap, request);
		//
		// 		}
		//
		// 	} catch (Exception e) {
		// 		log.info("======= 로그 저장 중에 에러가 발생하였습니다. ========== {}", e.getMessage());
		// 	} finally {
		// 		log.info("===================E N D===================");
		// 		// logUtils.init();
		// 	}
	}

	private void printJoinPoint(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();

		log.info("[[[ method : {} , className : {}", signature.getName(), signature.getDeclaringTypeName());

		Object targetObject = joinPoint.getTarget();
		log.info("[[[ target object : {} ", targetObject.toString());

		Object thisObject = joinPoint.getThis();
		log.info("[[[ proxy Object : {} ", thisObject.toString());

		Object[] arguments = joinPoint.getArgs();
		if (arguments != null && arguments.length > 0) {
			log.info("Paramas");
			for (int i = 0; i < arguments.length; i++) {
				log.info("param[{}] : {}", i, arguments[i]);
			}
		}
	}

}
