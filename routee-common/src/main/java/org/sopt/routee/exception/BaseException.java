package org.sopt.routee.exception;

import org.sopt.routee.code.ErrorResultCode;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
	private final ErrorResultCode code;

	protected BaseException(ErrorResultCode code) {
		super(code.getMessage());
		this.code = code;
	}

	protected BaseException(ErrorResultCode code, Throwable cause) {
		super(code.getMessage(), cause);
		this.code = code;
	}
}
