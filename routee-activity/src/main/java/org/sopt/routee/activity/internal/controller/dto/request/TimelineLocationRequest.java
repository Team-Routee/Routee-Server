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

	@NotNull(message = "elevation은 필수입니다.")
	@DecimalMin(value = "-500.0", message = "elevation은 -500.0 이상이어야 합니다.")
	@DecimalMax(value = "9000.0", message = "elevation은 9000.0 이하여야 합니다.")
	Double elevation,

	@NotNull(message = "pointIndex는 필수입니다.")
	Integer pointIndex
) {
}
