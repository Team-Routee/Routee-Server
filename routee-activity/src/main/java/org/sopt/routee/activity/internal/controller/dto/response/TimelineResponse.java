package org.sopt.routee.activity.internal.controller.dto.response;

import java.time.Instant;

import org.sopt.routee.activity.internal.service.dto.result.TimelineResult;

public record TimelineResponse(
	Long timelineId,
	String title,
	String imageUrl,
	Instant createdAt
) {
	public static TimelineResponse from(TimelineResult result) {
		return new TimelineResponse(result.timelineId(), result.title(), result.imageUrl(), result.createdAt());
	}
}
