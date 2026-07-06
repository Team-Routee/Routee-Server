package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.CreateRouteCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RouteRequest(
	@NotBlank(message = "name은 필수입니다.")
	String name,

	@NotNull(message = "sequence는 필수입니다.")
	@PositiveOrZero(message = "sequence는 0 이상이어야 합니다.")
	Integer sequence
) {
	public CreateRouteCommand toCommand() {
		return new CreateRouteCommand(name, sequence);
	}
}
