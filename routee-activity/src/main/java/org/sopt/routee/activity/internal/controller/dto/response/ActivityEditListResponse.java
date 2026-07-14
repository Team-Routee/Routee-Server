package org.sopt.routee.activity.internal.controller.dto.response;

import java.util.List;

import org.sopt.routee.activity.internal.service.dto.result.ActivityEditListResult;

public record ActivityEditListResponse(
	int year,
	int month,
	List<ActivityEditItemResponse> activities
) {
	public static ActivityEditListResponse from(ActivityEditListResult result) {
		List<ActivityEditItemResponse> activities = result.activities().stream()
			.map(ActivityEditItemResponse::from)
			.toList();
		return new ActivityEditListResponse(result.year(), result.month(), activities);
	}
}
