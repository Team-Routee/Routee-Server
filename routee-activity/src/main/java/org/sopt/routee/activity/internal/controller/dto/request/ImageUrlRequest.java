package org.sopt.routee.activity.internal.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ImageUrlRequest(
	@NotBlank(message = "fileName은 필수입니다.")
	String fileName
) {
}
