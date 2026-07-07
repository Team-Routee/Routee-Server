package org.sopt.routee.activity.internal.code;

import org.sopt.routee.code.ErrorResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorResultCode {

	ALREADY_IN_PROGRESS_ACTIVITY(HttpStatus.CONFLICT, "이미 진행 중인 활동이 있습니다.");

	private final HttpStatus status;
	private final String message;
}
