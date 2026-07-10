package org.sopt.routee.activity.internal.service.dto.result;

import java.util.List;

public record ActivityRecapResult(
	Integer distance,
	Integer durationSec,
	Integer maxElevation,
	String mapImageUrl,
	List<ActivityRecapRouteResult> routes
) {
}
