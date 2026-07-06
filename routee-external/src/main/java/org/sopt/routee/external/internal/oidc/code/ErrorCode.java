package org.sopt.routee.external.internal.oidc.code;

import org.sopt.routee.code.ErrorResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorResultCode {

	INVALID_AUDIENCE(HttpStatus.BAD_REQUEST, "올바르지 않은 audience입니다."),
	ID_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 id_token입니다."),
	INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 id_token입니다."),
	INVALID_TOKEN_CLAIMS(HttpStatus.UNAUTHORIZED, "id_token 클레임이 유효하지 않습니다."),
	UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 제공자입니다.");

	private final HttpStatus status;
	private final String message;
}