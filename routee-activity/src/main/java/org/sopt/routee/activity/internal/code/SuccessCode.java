package org.sopt.routee.activity.internal.code;

import org.sopt.routee.code.SuccessResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements SuccessResultCode {

	ACTIVITY_CREATED(HttpStatus.CREATED, "활동 기록 생성에 성공했습니다."),
	ACTIVITY_STATISTICS_GET_SUCCESS(HttpStatus.OK, "활동 통계 기록 조회에 성공했습니다."),
	TIMELINE_CREATED(HttpStatus.CREATED, "타임라인 생성에 성공했습니다."),
	TIMELINE_LIST_GET_SUCCESS(HttpStatus.OK, "타임라인 목록 조회에 성공했습니다."),
	ROUTE_LIST_CREATE_SUCCESS(HttpStatus.CREATED, "루트 목록 생성에 성공했습니다."),
	ROUTE_LIST_GET_SUCCESS(HttpStatus.OK, "루트 목록 조회에 성공했습니다."),
	IMAGE_UPLOAD_URL_CREATED(HttpStatus.OK, "이미지 업로드 URL 발급에 성공했습니다.");

	private final HttpStatus status;
	private final String message;
}
