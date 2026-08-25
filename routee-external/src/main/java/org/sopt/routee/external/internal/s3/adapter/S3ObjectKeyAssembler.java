package org.sopt.routee.external.internal.s3.adapter;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class S3ObjectKeyAssembler {

	private static final String MEMBER_ROOT_PATH = "member";
	private static final String ACTIVITY_SUB_PATH = "activity";

	static String assemble(
		FileUploadDirectory directory,
		FileUploadImageSize imageSize,
		String memberId,
		String activityId,
		String objectKey
	) {
		return switch (directory) {
			case TIMELINE -> "%s/%s/%s/%s/%s/%s/%s".formatted(
				MEMBER_ROOT_PATH,
				memberId,
				ACTIVITY_SUB_PATH,
				Objects.requireNonNull(activityId, "activityId must not be null for timeline"),
				directory.path(),
				Objects.requireNonNull(imageSize, "imageSize must not be null for timeline").path(),
				objectKey
			);
			case RECAP -> "%s/%s/%s/%s/%s/%s".formatted(
				MEMBER_ROOT_PATH,
				memberId,
				ACTIVITY_SUB_PATH,
				Objects.requireNonNull(activityId, "activityId must not be null for recap"),
				directory.path(),
				objectKey
			);
			case PROFILE -> "%s/%s/%s/%s".formatted(
				MEMBER_ROOT_PATH,
				memberId,
				directory.path(),
				objectKey
			);
		};
	}
}
