package org.sopt.routee.external.internal.oidc.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oidc.code.ErrorCode;

public final class InvalidOidcClaimsException extends BaseException {

	public InvalidOidcClaimsException() {
		super(ErrorCode.INVALID_TOKEN_CLAIMS);
	}
}