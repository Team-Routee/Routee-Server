package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.UpdateActivityTitleResult;

public record ActivityTitleResponse(
	Long activityId,
	String title
) {
	public static ActivityTitleResponse from(UpdateActivityTitleResult result) {
		return new ActivityTitleResponse(result.activityId(), result.title());
	}
}
