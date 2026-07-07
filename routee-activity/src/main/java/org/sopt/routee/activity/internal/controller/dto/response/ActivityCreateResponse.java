package org.sopt.routee.activity.internal.controller.dto.response;

public record ActivityCreateResponse(
	Long activityId
) {
	public static ActivityCreateResponse from(Long activityId) {
		return new ActivityCreateResponse(activityId);
	}
}
