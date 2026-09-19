package org.sopt.routee.member.internal.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.member.internal.code.ErrorCode;

public class AuthorizationCodeRequiredException extends BaseException {

	public AuthorizationCodeRequiredException() {
		super(ErrorCode.AUTHORIZATION_CODE_REQUIRED);
	}
}
