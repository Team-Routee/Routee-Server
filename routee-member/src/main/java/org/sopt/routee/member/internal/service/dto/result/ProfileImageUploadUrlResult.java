package org.sopt.routee.member.internal.service.dto.result;

public record ProfileImageUploadUrlResult(
	String presignedUrl,
	String objectKey
) {
}
