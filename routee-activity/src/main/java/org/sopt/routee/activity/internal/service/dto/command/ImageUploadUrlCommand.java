package org.sopt.routee.activity.internal.service.dto.command;

import org.sopt.routee.external.api.type.FileUploadImageSize;

public record ImageUploadUrlCommand(
	Long activityId,
	Long memberId,
	String fileName,
	FileUploadImageSize imageSize
) {
}
