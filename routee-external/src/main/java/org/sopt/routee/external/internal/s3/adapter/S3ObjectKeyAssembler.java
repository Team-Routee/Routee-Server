package org.sopt.routee.external.internal.s3.adapter;

import java.util.Objects;

import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;

final class S3ObjectKeyAssembler {

	private static final String ACTIVITY_ROOT_PATH = "activity";

	private S3ObjectKeyAssembler() {
	}

	static String assemble(
		FileUploadDirectory directory,
		FileUploadImageSize imageSize,
		String activityId,
		String objectKey
	) {
		return switch (directory) {
			case TIMELINE -> "%s/%s/%s/%s".formatted(
				activityId,
				directory.path(),
				Objects.requireNonNull(imageSize, "imageSize must not be null for timeline").path(),
				objectKey
			);
			case RECAP -> "%s/%s/%s".formatted(
				activityId,
				directory.path(),
				objectKey
			);
		};
	}

	static String assembleUploadObjectKey(
		FileUploadDirectory directory,
		FileUploadImageSize imageSize,
		String activityId,
		String objectKey
	) {
		return "%s/%s".formatted(
			ACTIVITY_ROOT_PATH,
			assemble(directory, imageSize, activityId, objectKey)
		);
	}
}
