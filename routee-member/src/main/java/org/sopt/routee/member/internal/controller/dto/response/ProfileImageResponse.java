package org.sopt.routee.member.internal.controller.dto.response;

import org.sopt.routee.member.internal.service.dto.result.UpdateProfileImageResult;

public record ProfileImageResponse(
	String profileImageUrl
) {
	public static ProfileImageResponse from(UpdateProfileImageResult result) {
		return new ProfileImageResponse(result.profileImageUrl());
	}
}
