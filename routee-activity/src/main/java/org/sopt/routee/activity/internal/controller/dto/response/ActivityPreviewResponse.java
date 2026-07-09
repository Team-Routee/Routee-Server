package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.ActivityPreviewResult;

public record ActivityPreviewResponse(
	Long activityId,
	String title,
	String thumbnailUrl
) {
	public static ActivityPreviewResponse from(ActivityPreviewResult result) {
		return new ActivityPreviewResponse(
			result.activityId(),
			result.title(),
			result.thumbnailUrl()
		);
	}
}
