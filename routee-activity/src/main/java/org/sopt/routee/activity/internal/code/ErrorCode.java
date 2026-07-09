package org.sopt.routee.activity.internal.code;

import org.sopt.routee.code.ErrorResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorResultCode {

	ALREADY_IN_PROGRESS_ACTIVITY(HttpStatus.CONFLICT, "이미 진행 중이거나 일시정지된 활동이 있습니다."),
	ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "활동 기록이 존재하지 않습니다."),
	ROUTE_ALREADY_EXISTS(HttpStatus.CONFLICT, "루트 목록이 이미 존재합니다."),
	INVALID_TIME_ZONE(HttpStatus.BAD_REQUEST, "Time-Zone 헤더 값이 올바르지 않습니다."),
	UNSUPPORTED_IMAGE_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 파일 확장자입니다."),
	INVALID_ACTIVITY_STATUS_TRANSITION(HttpStatus.CONFLICT, "변경할 수 없는 활동 상태입니다."),
	ACTIVITY_STATUS_ALREADY_SAME(HttpStatus.CONFLICT, "이미 동일한 활동 상태입니다."),
	ACTIVITY_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 활동입니다.");

	private final HttpStatus status;
	private final String message;
}
