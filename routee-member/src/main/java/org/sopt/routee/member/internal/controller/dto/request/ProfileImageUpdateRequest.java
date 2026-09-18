package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.UpdateProfileImageCommand;

import jakarta.validation.constraints.NotBlank;

public record ProfileImageUpdateRequest(
	@NotBlank(message = "objectKey는 필수입니다.")
	String objectKey
) {
	public UpdateProfileImageCommand toCommand(Long memberId) {
		return new UpdateProfileImageCommand(memberId, objectKey);
	}
}
