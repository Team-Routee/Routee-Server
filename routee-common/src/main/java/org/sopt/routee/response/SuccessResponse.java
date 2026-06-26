package org.sopt.routee.response;

import org.sopt.routee.code.SuccessResultCode;

public record SuccessResponse<T>(
	int status,
	String code,
	String message,
	T data
) implements ApiResponse {
	public static <T> SuccessResponse<T> of(SuccessResultCode resultCode, T data) {
		return new SuccessResponse<>(resultCode.getStatus().value(), resultCode.toString(), resultCode.getMessage(), data);
	}

	public static <T> SuccessResponse<T> of(SuccessResultCode resultCode) {
		return new SuccessResponse<>(resultCode.getStatus().value(), resultCode.toString(), resultCode.getMessage(), null);
	}
}
