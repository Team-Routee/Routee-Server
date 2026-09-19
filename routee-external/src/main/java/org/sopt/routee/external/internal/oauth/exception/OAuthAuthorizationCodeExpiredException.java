package org.sopt.routee.external.internal.oauth.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oauth.code.ErrorCode;

public final class OAuthAuthorizationCodeExpiredException extends BaseException {

	public OAuthAuthorizationCodeExpiredException(Throwable cause) {
		super(ErrorCode.AUTHORIZATION_CODE_EXPIRED, cause);
	}
}
