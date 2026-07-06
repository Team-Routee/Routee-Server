package org.sopt.routee.activity.internal.code;

import org.sopt.routee.code.ErrorResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorResultCode {

	ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "활동 기록이 존재하지 않습니다.");

	private final HttpStatus status;
	private final String message;
}
