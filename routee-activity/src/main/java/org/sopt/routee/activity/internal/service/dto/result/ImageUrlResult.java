package org.sopt.routee.activity.internal.service.dto.result;

public record ImageUrlResult(
	String presignedUrl,
	String objectKey
) {
}
