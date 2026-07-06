package org.sopt.routee.external.internal.oidc.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oidc.code.ErrorCode;

public final class ExpiredOidcTokenException extends BaseException {

	public ExpiredOidcTokenException() {
		super(ErrorCode.ID_TOKEN_EXPIRED);
	}
}