package org.sopt.routee.exception.handler;

import org.sopt.routee.code.ErrorResultCode;
import org.sopt.routee.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseExceptionHandler {
	protected final ResponseEntity<ApiResponse> buildErrorResponse(ErrorResultCode errorResultCode) {
		return ResponseEntity.status(errorResultCode.getStatus())
			.body(ApiResponse.failure(errorResultCode));
	}

	protected final ResponseEntity<ApiResponse> buildErrorResponse(ErrorResultCode errorResultCode, String message) {
		return ResponseEntity.status(errorResultCode.getStatus())
			.body(ApiResponse.failure(errorResultCode, message));
	}
}