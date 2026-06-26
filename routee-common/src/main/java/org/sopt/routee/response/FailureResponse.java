package org.sopt.routee.response;

import org.sopt.routee.code.ErrorResultCode;

public record FailureResponse(
	int status,
	String code,
	String message
) implements ApiResponse {
	public static FailureResponse of(ErrorResultCode resultCode) {
		return new FailureResponse(resultCode.getStatus().value(), resultCode.toString(), resultCode.getMessage());
	}

	public static FailureResponse of(ErrorResultCode resultCode, String message) {
		return new FailureResponse(resultCode.getStatus().value(), resultCode.toString(), message);
	}
}
