package com.bwc.common.controller;

import java.util.Locale;

import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.constant.ValidCodeEnum;
import com.bwc.common.exception.MessageGateException;
import com.bwc.common.model.ErrorCodeModel;
import com.bwc.common.util.StrUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ExceptionHandler.java
 * @Description : 클래스 설명을 기술합니다.
 * @author 451773
 * @since 2024. 01. 24.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2024. 01. 24.     451773     	     최초 생성
 * </pre>
 */

@RestControllerAdvice
@RestController
@Slf4j
public class MessageGateExceptionHandler {
	@Resource(name = "messageSource")
	private MessageSource messageSource;

	/**
	 *
	 * 404 및  500 에러시, restApi
	 *
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/error/{code}")
	public ResponseEntity<?> ErrorHandler(@PathVariable(value = "code") String code) {
		HttpStatus status = null;
		ErrorCodeModel errorCode = new ErrorCodeModel();

		if (code.equals("400")) {
			errorCode.of(ErrorCodeEnum.BAD_REQUEST);
			status = ErrorCodeEnum.BAD_REQUEST.getHttpSatus();
		} else if (code.equals("401")) {
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			String authorization = request.getHeader("authorization");

			if (StrUtil.isEmpty(authorization)) {
				log.info("@ authorization 이 들어오지 않았습니다.");
			} else {
				log.info("@ authorization 가 잘못된 값으로 내려오고 있습니다. : {}", authorization);
			}

			errorCode.of(ErrorCodeEnum.UNAUTHORIZED);
			status = ErrorCodeEnum.UNAUTHORIZED.getHttpSatus();

		} else if (code.equals("404")) {
			errorCode.of(ErrorCodeEnum.NOT_FOUND);
			status = ErrorCodeEnum.NOT_FOUND.getHttpSatus();

		} else if (code.equals("405")) {
			errorCode.of(ErrorCodeEnum.NOT_ALLOWED);
			status = ErrorCodeEnum.NOT_ALLOWED.getHttpSatus();

		} else if (code.equals("500")) {
			errorCode.of(ErrorCodeEnum.INTERSERVER_ERROR);
			status = ErrorCodeEnum.INTERSERVER_ERROR.getHttpSatus();
		}

		return new ResponseEntity<>(errorCode, status);
	}

	/**
	 *
	 * 필수 값 에러 시 API
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> ExceptionmethodArgument(MethodArgumentNotValidException e) {
		String sessGuid = MDC.get("sessGuid");
		log.info("[{}] ####### ExceptionmethodArgument 예외 발생", sessGuid);

		FieldError error = e.getBindingResult().getFieldError();
		String errorCode = error.getDefaultMessage();
		String validMessage = ValidCodeEnum.getMsg(errorCode);
		ErrorCodeModel response = new ErrorCodeModel()
			.builder()
			.resultCode(errorCode)
			.resultMessage(validMessage)
			.build();

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 *  잘못된 HTTP Method, JSON으로 파라미터가 아닐떄
	 */
	@SuppressWarnings("static-access")
	@ExceptionHandler(value = {HttpMessageNotReadableException.class,
		HttpRequestMethodNotSupportedException.class})
	public ResponseEntity<?> ExceptionMessageConvert(MethodArgumentNotValidException e) {
		log.info("========= 파라미터 읽어 오는 중에 에러가 발생하였습니다. =========");

		ErrorCodeModel response = new ErrorCodeModel()
			.builder()
			.resultCode(ErrorCodeEnum.REQUEST_FAIL.getCode())
			.resultMessage(
				messageSource.getMessage(ErrorCodeEnum.REQUEST_FAIL.getMessage(), new String[] {}, Locale.getDefault()))
			.build();

		return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);

	}

	/**
	 *
	 * 사용자 정의 에러 메시지 응답
	 *
	 * @param exception
	 * @return
	 */
	@SuppressWarnings("static-access")
	@ExceptionHandler(MessageGateException.class)
	public ResponseEntity<?> messageGateException(MessageGateException exception) {
		log.info("=========== API 도중에 에러가 발생하였습니다. ============");

		ErrorCodeModel response = new ErrorCodeModel()
			.builder()
			.resultCode(exception.getResultCode())
			.resultMessage(exception.getResultMessage())
			.build();

		return new ResponseEntity<>(response, exception.getStatus());

	}

}
