package org.sopt.routee.auth.internal.exception;

import org.sopt.routee.auth.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class ForbiddenException extends BaseException {
	public ForbiddenException() {
		super(ErrorCode.FORBIDDEN);
	}
}
