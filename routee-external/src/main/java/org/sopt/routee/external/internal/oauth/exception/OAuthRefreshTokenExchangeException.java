package org.sopt.routee.external.internal.oauth.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oauth.code.ErrorCode;

public final class OAuthRefreshTokenExchangeException extends BaseException {

	public OAuthRefreshTokenExchangeException() {
		super(ErrorCode.OAUTH_REFRESH_TOKEN_EXCHANGE_FAILED);
	}

	public OAuthRefreshTokenExchangeException(Throwable cause) {
		super(ErrorCode.OAUTH_REFRESH_TOKEN_EXCHANGE_FAILED, cause);
	}
}
