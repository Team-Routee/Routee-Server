package org.sopt.routee.activity.internal.mapper;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.route.Route;
import org.sopt.routee.activity.internal.service.dto.command.CreateRouteCommand;
import org.sopt.routee.activity.internal.service.dto.result.RouteResult;

public class RouteMapper {

	public static Route toEntity(CreateRouteCommand command, Activity activity) {
		return Route.builder()
			.name(command.name())
			.sequence(command.sequence())
			.activity(activity)
			.build();
	}

	public static RouteResult toResult(Route route) {
		return new RouteResult(route.getId(), route.getName(), route.getSequence());
	}
}
