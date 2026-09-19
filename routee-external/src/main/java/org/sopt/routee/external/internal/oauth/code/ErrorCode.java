package org.sopt.routee.external.internal.oauth.code;

import org.sopt.routee.code.ErrorResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorResultCode {

	OAUTH_REVOKE_FAILED(HttpStatus.BAD_GATEWAY, "소셜 로그인 연동 해제에 실패했습니다."),
	OAUTH_REFRESH_TOKEN_EXCHANGE_FAILED(HttpStatus.BAD_GATEWAY, "소셜 로그인 refresh_token 교환에 실패했습니다."),
	AUTHORIZATION_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, "만료되었거나 유효하지 않은 authorization_code입니다."),
	APPLE_CLIENT_SECRET_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Apple client secret 생성에 실패했습니다.");

	private final HttpStatus status;
	private final String message;
}
