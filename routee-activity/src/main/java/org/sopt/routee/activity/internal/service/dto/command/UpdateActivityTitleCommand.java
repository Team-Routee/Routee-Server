package org.sopt.routee.activity.internal.service.dto.command;

public record UpdateActivityTitleCommand(
	Long activityId,
	Long memberId,
	String title
) {
}
