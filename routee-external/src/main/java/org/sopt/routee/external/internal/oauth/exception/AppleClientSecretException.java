package org.sopt.routee.external.internal.oauth.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oauth.code.ErrorCode;

public final class AppleClientSecretException extends BaseException {

	public AppleClientSecretException(Throwable cause) {
		super(ErrorCode.APPLE_CLIENT_SECRET_GENERATION_FAILED, cause);
	}
}
