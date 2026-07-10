package org.sopt.routee.activity.internal.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

import org.sopt.routee.activity.internal.service.dto.result.ActivitiesByDateResult;

public record ActivitiesByDateResponse(
	LocalDate date,
	List<ActivityPreviewResponse> activities
) {
	public static ActivitiesByDateResponse from(ActivitiesByDateResult result) {
		List<ActivityPreviewResponse> activities = result.activities().stream()
			.map(ActivityPreviewResponse::from)
			.toList();
		return new ActivitiesByDateResponse(result.date(), activities);
	}
}
