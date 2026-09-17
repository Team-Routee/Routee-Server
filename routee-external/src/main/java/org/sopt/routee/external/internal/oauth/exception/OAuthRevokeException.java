package org.sopt.routee.external.internal.oauth.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oauth.code.ErrorCode;

public final class OAuthRevokeException extends BaseException {

	public OAuthRevokeException() {
		super(ErrorCode.OAUTH_REVOKE_FAILED);
	}

	public OAuthRevokeException(Throwable cause) {
		super(ErrorCode.OAUTH_REVOKE_FAILED, cause);
	}
}
