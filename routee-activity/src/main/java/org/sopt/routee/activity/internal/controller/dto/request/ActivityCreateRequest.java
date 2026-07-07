package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.entity.activity.ActivityType;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;

import jakarta.validation.constraints.NotNull;

public record ActivityCreateRequest(
	@NotNull(message = "activityType은 필수입니다.")
	ActivityType activityType
) {
	public CreateActivityCommand toCommand(Long memberId) {
		return new CreateActivityCommand(memberId, activityType);
	}
}
