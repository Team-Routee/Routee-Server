package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.ImageUploadUrlCommand;
import org.sopt.routee.external.api.type.FileUploadImageType;

import jakarta.validation.constraints.NotBlank;

public record ImageUrlRequest(
	@NotBlank(message = "fileName은 필수입니다.")
	String fileName
) {

	public ImageUploadUrlCommand toCommand(Long activityId, Long memberId, FileUploadImageType imageType) {
		return new ImageUploadUrlCommand(activityId, memberId, fileName, imageType);
	}
}
