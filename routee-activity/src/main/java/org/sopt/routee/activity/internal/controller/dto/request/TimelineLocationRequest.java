package org.sopt.routee.activity.internal.controller.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
	@Min(value = -500, message = "elevation은 -500 이상이어야 합니다.")
	@Max(value = 9000, message = "elevation은 9000 이하여야 합니다.")
	Integer elevation,

	@NotNull(message = "pointIndex는 필수입니다.")
	@Min(1)
	Integer pointIndex
) {
}
