package org.sopt.routee.activity.internal.service.dto.command;

import org.sopt.routee.external.api.type.FileUploadImageType;

public record ImageUploadUrlCommand(
	Long activityId,
	Long memberId,
	String fileName,
	FileUploadImageType imageType
) {
}
