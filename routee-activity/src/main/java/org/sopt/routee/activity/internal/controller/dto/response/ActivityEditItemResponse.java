package org.sopt.routee.activity.internal.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

import org.sopt.routee.activity.internal.service.dto.result.ActivityEditItemResult;

public record ActivityEditItemResponse(
	Long activityId,
	String title,
	LocalDate activityDate,
	List<String> timelinePhotoUrls
) {
	public static ActivityEditItemResponse from(ActivityEditItemResult result) {
		return new ActivityEditItemResponse(
			result.activityId(),
			result.title(),
			result.activityDate(),
			result.timelinePhotoUrls()
		);
	}
}
