package org.sopt.routee.external.internal.oidc.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.oidc.code.ErrorCode;

public final class UnsupportedOidcProviderException extends BaseException {

	public UnsupportedOidcProviderException() {
		super(ErrorCode.UNSUPPORTED_PROVIDER);
	}
}