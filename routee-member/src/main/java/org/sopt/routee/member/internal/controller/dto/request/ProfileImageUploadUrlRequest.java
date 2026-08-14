package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.ProfileImageUploadUrlCommand;

import jakarta.validation.constraints.NotBlank;

public record ProfileImageUploadUrlRequest(
	@NotBlank(message = "fileName은 필수입니다.")
	String fileName
) {
	public ProfileImageUploadUrlCommand toCommand(Long memberId) {
		return new ProfileImageUploadUrlCommand(memberId, fileName);
	}
}
