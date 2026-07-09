package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.TimelineMarkerResult;

public record TimelineMarkerResponse(
	Long timelineId,
	String thumbnailUrl,
	double latitude,
	double longitude
) {
	public static TimelineMarkerResponse from(TimelineMarkerResult result) {
		return new TimelineMarkerResponse(result.timelineId(), result.thumbnailUrl(), result.latitude(), result.longitude());
	}
}
