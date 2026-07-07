package org.sopt.routee.activity.internal.code;

import org.sopt.routee.code.SuccessResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements SuccessResultCode {

	ACTIVITY_CREATED(HttpStatus.CREATED, "활동 기록 생성에 성공했습니다.");

	private final HttpStatus status;
	private final String message;
}
