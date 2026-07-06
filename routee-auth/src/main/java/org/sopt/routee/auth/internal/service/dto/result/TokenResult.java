package org.sopt.routee.auth.internal.service.dto.result;

public record TokenResult(
	String accessToken,
	String refreshToken,
	String tokenType
) {
}
