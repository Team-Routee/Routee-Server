package org.sopt.routee.external.internal.oidc.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oidc.code.ErrorCode;

public final class InvalidOidcTokenException extends BaseException {

	public InvalidOidcTokenException(Throwable cause) {
		super(ErrorCode.INVALID_ID_TOKEN, cause);
	}
}