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
	REQUIRED_AGREEMENT_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "필수 약관에 모두 동의해야 합니다."),
	INVALID_TIME_ZONE(HttpStatus.BAD_REQUEST, "Time-Zone 헤더 값이 올바르지 않습니다."),
	UNSUPPORTED_IMAGE_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 파일 확장자입니다."),
	OAUTH_CREDENTIAL_ENCRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인 자격 증명 암복호화에 실패했습니다."),
	AUTHORIZATION_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "저장된 소셜 로그인 연동 정보가 없어 authorization_code가 필요합니다.");

	private final HttpStatus status;
	private final String message;
}