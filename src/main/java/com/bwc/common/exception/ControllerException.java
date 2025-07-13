package com.bwc.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControllerException {

	//	@ExceptionHandler({TemplateInputException.class, Exception.class})
	//	public String handle(Exception ex) {
	//
	//		log.error("ControllerException ::" + ex.getMessage());
	//
	//		return "error";
	//	}
}
