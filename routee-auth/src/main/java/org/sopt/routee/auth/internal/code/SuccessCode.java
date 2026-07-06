package org.sopt.routee.auth.internal.code;

import org.sopt.routee.code.SuccessResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements SuccessResultCode {

	LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
	TOKEN_REISSUE(HttpStatus.OK, "토큰 재발급에 성공했습니다.");

	private final HttpStatus status;
	private final String message;
}