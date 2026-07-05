package org.sopt.routee.auth.internal.exception;

import org.sopt.routee.auth.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class InvalidTokenException extends BaseException {

	public InvalidTokenException() {
		super(ErrorCode.INVALID_TOKEN);
	}
}
