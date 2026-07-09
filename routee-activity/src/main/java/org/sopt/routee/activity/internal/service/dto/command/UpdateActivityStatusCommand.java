package org.sopt.routee.activity.internal.service.dto.command;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;

public record UpdateActivityStatusCommand(
	Long activityId,
	Long memberId,
	ActivityStatus status
) {
}
