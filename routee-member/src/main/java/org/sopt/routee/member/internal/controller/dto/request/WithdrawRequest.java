package org.sopt.routee.member.internal.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
	@NotBlank(message = "refresh_token은 필수입니다.")
	String refreshToken
) {
}
