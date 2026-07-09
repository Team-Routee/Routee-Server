package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.ActivityRecapRouteResult;

public record ActivityRecapRouteResponse(
	Integer sequence,
	String name
) {
	public static ActivityRecapRouteResponse from(ActivityRecapRouteResult result) {
		return new ActivityRecapRouteResponse(result.sequence(), result.name());
	}
}
