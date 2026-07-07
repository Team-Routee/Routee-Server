package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;

public record ActivityCreateResponse(
	Long activityId,
	String title
) {
	public static ActivityCreateResponse from(CreateActivityResult result) {
		return new ActivityCreateResponse(result.activityId(), result.title());
	}
}
