package org.sopt.routee.activity.internal.service.dto.result;

public record ActivityPreviewResult(
	Long activityId,
	String title,
	String thumbnailUrl
) {
}
