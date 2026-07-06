package org.sopt.routee.auth.internal.exception;

import org.sopt.routee.auth.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class UnauthorizedException extends BaseException {
	public UnauthorizedException() {
		super(ErrorCode.UNAUTHORIZED);
	}
}
