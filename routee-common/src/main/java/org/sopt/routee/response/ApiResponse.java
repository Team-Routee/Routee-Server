package org.sopt.routee.response;

import org.sopt.routee.code.ErrorResultCode;
import org.sopt.routee.code.SuccessResultCode;

public interface ApiResponse {
	int status();

	String code();

	String message();

	static <T> SuccessResponse<T> success(SuccessResultCode resultCode, T data) {
		return SuccessResponse.of(resultCode, data);
	}

	static <T> SuccessResponse<T> success(SuccessResultCode resultCode) {
		return SuccessResponse.of(resultCode);
	}

	static FailureResponse failure(ErrorResultCode resultCode) {
		return FailureResponse.of(resultCode);
	}

	static FailureResponse failure(ErrorResultCode resultCode, String message) {
		return FailureResponse.of(resultCode, message);
	}
}
