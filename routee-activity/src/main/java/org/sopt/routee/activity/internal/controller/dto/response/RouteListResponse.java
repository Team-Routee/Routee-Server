package org.sopt.routee.activity.internal.controller.dto.response;

import java.util.List;

import org.sopt.routee.activity.internal.service.dto.result.RouteResult;

public record RouteListResponse(
	Long activityId,
	List<RouteResponse> routes
) {
	public static RouteListResponse of(Long activityId, List<RouteResult> results) {
		List<RouteResponse> routes = results.stream()
			.map(RouteResponse::of)
			.toList();
		return new RouteListResponse(activityId, routes);
	}
}
