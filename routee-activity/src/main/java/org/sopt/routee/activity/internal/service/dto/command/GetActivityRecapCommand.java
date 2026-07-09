package org.sopt.routee.activity.internal.service.dto.command;

public record GetActivityRecapCommand(
	Long activityId,
	Long memberId
) {
	public static GetActivityRecapCommand of(Long activityId, Long memberId) {
		return new GetActivityRecapCommand(activityId, memberId);
	}
}
