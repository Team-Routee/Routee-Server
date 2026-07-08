package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;

public record ImageUrlResponse(
	String presignedUrl,
	String objectKey
) {
	public static ImageUrlResponse of(ImageUrlResult result) {
		return new ImageUrlResponse(result.presignedUrl(), result.objectKey());
	}
}
