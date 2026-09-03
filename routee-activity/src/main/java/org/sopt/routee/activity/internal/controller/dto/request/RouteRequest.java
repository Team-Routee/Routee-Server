package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.CreateRouteCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RouteRequest(
	@NotBlank(message = "name은 필수입니다.")
	@Size(max = 16, message = "name은 16자 이하여야 합니다.")
	String name,

	@NotNull(message = "sequence는 필수입니다.")
	@Positive(message = "sequence는 1 이상이어야 합니다.")
	Integer sequence
) {
	public CreateRouteCommand toCommand() {
		return new CreateRouteCommand(name, sequence);
	}
}
