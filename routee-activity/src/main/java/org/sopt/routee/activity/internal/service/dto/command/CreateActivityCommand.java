package org.sopt.routee.activity.internal.service.dto.command;

import org.sopt.routee.activity.internal.entity.activity.ActivityType;

public record CreateActivityCommand(
	Long memberId,
	ActivityType activityType
) {
}
