package org.sopt.routee.auth.internal.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
	@NotBlank(message = "refresh_token은 필수입니다.")
	String refreshToken
) {
}
