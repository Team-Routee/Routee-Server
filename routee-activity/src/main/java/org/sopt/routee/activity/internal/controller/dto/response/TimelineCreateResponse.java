package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.CreateTimelineResult;

public record TimelineCreateResponse(
	Long timelineId
) {
	public static TimelineCreateResponse from(CreateTimelineResult result) {
		return new TimelineCreateResponse(result.timelineId());
	}
}
