package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.ImageUploadUrlCommand;

import jakarta.validation.constraints.NotBlank;

public record ImageUrlRequest(
	@NotBlank(message = "fileName은 필수입니다.")
	String fileName
) {

	public ImageUploadUrlCommand toCommand(Long activityId, Long memberId) {
		return new ImageUploadUrlCommand(activityId, memberId, fileName);
	}
}
