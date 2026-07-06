package org.sopt.routee.activity.internal.controller.dto.request;

import java.util.List;

import org.sopt.routee.activity.internal.service.dto.command.CreateRouteCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record CreateRoutesRequest(
	@Valid
	@Size(max = 12, message = "루트는 최대 12개까지 등록할 수 있습니다.")
	List<RouteRequest> routes
) {
	public CreateRoutesRequest {
		if (routes == null) {
			routes = List.of();
		}
	}

	public List<CreateRouteCommand> toCommands() {
		return routes.stream()
			.map(RouteRequest::toCommand)
			.toList();
	}
}
