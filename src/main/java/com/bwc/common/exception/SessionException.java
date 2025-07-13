package com.bwc.common.exception;

import org.hibernate.HibernateException;

public class SessionException extends HibernateException {
	public SessionException(String message) {
		super(message);
	}

	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}
}