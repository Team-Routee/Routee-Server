package org.sopt.routee.member.internal.controller.dto.response;

import org.sopt.routee.member.internal.service.dto.result.ProfileImageUploadUrlResult;

public record ProfileImageUploadUrlResponse(
	String presignedUrl,
	String objectKey
) {
	public static ProfileImageUploadUrlResponse from(ProfileImageUploadUrlResult result) {
		return new ProfileImageUploadUrlResponse(result.presignedUrl(), result.objectKey());
	}
}
