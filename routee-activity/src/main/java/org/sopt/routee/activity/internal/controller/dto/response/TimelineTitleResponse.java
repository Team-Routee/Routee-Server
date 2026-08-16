package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.UpdateTimelineTitleResult;

public record TimelineTitleResponse(
	Long timelineId,
	String title
) {
	public static TimelineTitleResponse from(UpdateTimelineTitleResult result) {
		return new TimelineTitleResponse(result.timelineId(), result.title());
	}
}
