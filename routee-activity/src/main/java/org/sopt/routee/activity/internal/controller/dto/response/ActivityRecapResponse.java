package org.sopt.routee.activity.internal.controller.dto.response;

import java.util.List;

import org.sopt.routee.activity.internal.service.dto.result.ActivityRecapResult;

public record ActivityRecapResponse(
	Integer distance,
	Integer durationSec,
	Integer maxElevation,
	String mapImageUrl,
	List<ActivityRecapRouteResponse> routes
) {
	public static ActivityRecapResponse from(ActivityRecapResult result) {
		List<ActivityRecapRouteResponse> routes = result.routes().stream()
			.map(ActivityRecapRouteResponse::from)
			.toList();

		return new ActivityRecapResponse(
			result.distance(),
			result.durationSec(),
			result.maxElevation(),
			result.mapImageUrl(),
			routes
		);
	}
}
