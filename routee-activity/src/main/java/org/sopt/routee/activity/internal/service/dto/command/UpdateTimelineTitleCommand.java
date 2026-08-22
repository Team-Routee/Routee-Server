package org.sopt.routee.activity.internal.service.dto.command;

public record UpdateTimelineTitleCommand(
	Long activityId,
	Long timelineId,
	Long memberId,
	String title
) {
}
