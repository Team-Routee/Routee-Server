package org.sopt.routee.member.internal.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.member.internal.code.ErrorCode;

public class OAuthCredentialEncryptionException extends BaseException {

	public OAuthCredentialEncryptionException(Throwable cause) {
		super(ErrorCode.OAUTH_CREDENTIAL_ENCRYPTION_FAILED, cause);
	}
}
