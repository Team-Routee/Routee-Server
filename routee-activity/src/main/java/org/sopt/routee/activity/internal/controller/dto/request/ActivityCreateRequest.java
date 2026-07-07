package org.sopt.routee.activity.internal.controller.dto.request;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.activity.ActivityType;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;

import jakarta.validation.constraints.NotNull;

public record ActivityCreateRequest(
	@NotNull(message = "activityType은 필수입니다.")
	ActivityType activityType,

	@NotNull(message = "startedAt은 필수입니다.")
	LocalDateTime startedAt
) {
	public CreateActivityCommand toCommand(Long memberId, ZoneId timeZone) {
		return new CreateActivityCommand(memberId, activityType, startedAt, timeZone);
	}
}
