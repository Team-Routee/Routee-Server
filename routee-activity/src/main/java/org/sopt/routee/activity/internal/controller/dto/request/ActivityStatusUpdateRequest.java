package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.service.dto.command.UpdateActivityStatusCommand;

import jakarta.validation.constraints.NotNull;

public record ActivityStatusUpdateRequest(
	@NotNull(message = "status는 필수입니다.")
	ActivityStatus status
) {
	public UpdateActivityStatusCommand toCommand(Long activityId, Long memberId) {
		return new UpdateActivityStatusCommand(activityId, memberId, status);
	}
}
