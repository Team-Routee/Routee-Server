package org.sopt.routee.auth.internal.controller.dto.response;

import org.sopt.routee.auth.internal.service.dto.result.TokenResult;

public record TokenResponse(
	String accessToken,
	String refreshToken,
	String tokenType
) {
	public static TokenResponse of(TokenResult result) {
		return new TokenResponse(result.accessToken(), result.refreshToken(), result.tokenType());
	}
}
