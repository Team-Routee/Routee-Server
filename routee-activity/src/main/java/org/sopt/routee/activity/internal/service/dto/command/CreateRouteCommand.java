package org.sopt.routee.activity.internal.service.dto.command;

public record CreateRouteCommand(
	String name,
	int sequence
) {
}
