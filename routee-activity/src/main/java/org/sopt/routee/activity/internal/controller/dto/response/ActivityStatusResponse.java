package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.service.dto.result.UpdateActivityStatusResult;

public record ActivityStatusResponse(
	Long activityId,
	ActivityStatus status
) {
	public static ActivityStatusResponse from(UpdateActivityStatusResult result) {
		return new ActivityStatusResponse(result.activityId(), result.status());
	}
}
