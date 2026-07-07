package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.RouteResult;

public record RouteResponse(
	Long routeId,
	String name,
	int sequence
) {
	public static RouteResponse of(RouteResult result) {
		return new RouteResponse(result.routeId(), result.name(), result.sequence());
	}
}
