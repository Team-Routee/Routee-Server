package org.sopt.routee.activity.internal.controller.dto.response;

import java.util.List;

import org.sopt.routee.activity.internal.service.dto.result.TimelineResult;

public record TimelineListResponse(
	Long activityId,
	List<TimelineResponse> timelines
) {
	public static TimelineListResponse of(Long activityId, List<TimelineResult> results) {
		List<TimelineResponse> timelines = results.stream()
			.map(TimelineResponse::of)
			.toList();
		return new TimelineListResponse(activityId, timelines);
	}
}
