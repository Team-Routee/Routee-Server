package org.sopt.routee.activity.internal.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record TimelineLocationRequest(
	@NotNull(message = "longitude는 필수입니다.")
	Double longitude,

	@NotNull(message = "latitude는 필수입니다.")
	Double latitude,

	@NotNull(message = "altitude는 필수입니다.")
	Double altitude,

	@NotNull(message = "measure는 필수입니다.")
	Integer measure
) {
}
