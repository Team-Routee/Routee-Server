package org.sopt.routee.member.internal.code;

import org.sopt.routee.code.ErrorResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorResultCode {

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보가 존재하지 않습니다."),
	ALREADY_REGISTERED_MEMBER(HttpStatus.CONFLICT, "이미 가입된 회원입니다."),
	INVALID_TIME_ZONE(HttpStatus.BAD_REQUEST, "Time-Zone 헤더 값이 올바르지 않습니다.");

	private final HttpStatus status;
	private final String message;
}