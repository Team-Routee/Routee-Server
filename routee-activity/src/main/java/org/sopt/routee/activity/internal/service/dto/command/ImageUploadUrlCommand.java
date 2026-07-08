package org.sopt.routee.activity.internal.service.dto.command;

public record ImageUploadUrlCommand(
	Long activityId,
	Long memberId,
	String fileName
) {
}
