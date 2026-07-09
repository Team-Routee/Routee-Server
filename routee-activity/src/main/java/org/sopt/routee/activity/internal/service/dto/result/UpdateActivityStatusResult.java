package org.sopt.routee.activity.internal.service.dto.result;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;

public record UpdateActivityStatusResult(
	Long activityId,
	ActivityStatus status
) {
}
