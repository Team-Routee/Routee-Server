package org.sopt.routee.external.internal.s3.code;

import org.sopt.routee.code.ErrorResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorResultCode {

	FILE_UPLOAD_PRESIGN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 URL 생성에 실패했습니다.");

	private final HttpStatus status;
	private final String message;
}
