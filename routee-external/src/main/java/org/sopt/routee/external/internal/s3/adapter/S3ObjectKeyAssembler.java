package org.sopt.routee.external.internal.s3.adapter;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class S3ObjectKeyAssembler {

	private static final String ACTIVITY_ROOT_PATH = "activity";

	static String assemble(
		FileUploadDirectory directory,
		FileUploadImageSize imageSize,
		String ownerId,
		String objectKey
	) {
		return switch (directory) {
			case TIMELINE -> "%s/%s/%s/%s/%s".formatted(
				ACTIVITY_ROOT_PATH,
				ownerId,
				directory.path(),
				Objects.requireNonNull(imageSize, "imageSize must not be null for timeline").path(),
				objectKey
			);
			case RECAP -> "%s/%s/%s/%s".formatted(
				ACTIVITY_ROOT_PATH,
				ownerId,
				directory.path(),
				objectKey
			);
			case PROFILE -> "%s/%s/%s".formatted(
				directory.path(),
				ownerId,
				objectKey
			);
		};
	}
}
