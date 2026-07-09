package org.sopt.routee.activity.internal.controller.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TimelineLocationRequest(
	@NotNull(message = "longitude는 필수입니다.")
	@DecimalMin(value = "-180.0", message = "longitude는 -180.0 이상이어야 합니다.")
	@DecimalMax(value = "180.0", message = "longitude는 180.0 이하여야 합니다.")
	Double longitude,

	@NotNull(message = "latitude는 필수입니다.")
	@DecimalMin(value = "-90.0", message = "latitude는 -90.0 이상이어야 합니다.")
	@DecimalMax(value = "90.0", message = "latitude는 90.0 이하여야 합니다.")
	Double latitude,

	@NotNull(message = "altitude는 필수입니다.")
	@DecimalMin(value = "-500.0", message = "altitude는 -500.0 이상이어야 합니다.")
	@DecimalMax(value = "9000.0", message = "altitude는 9000.0 이하여야 합니다.")
	Double altitude,

	@NotNull(message = "measure는 필수입니다.")
	Integer measure
) {
}
