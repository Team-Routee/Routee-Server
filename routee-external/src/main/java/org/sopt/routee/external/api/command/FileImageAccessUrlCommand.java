package org.sopt.routee.external.api.command;

import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;

public record FileImageAccessUrlCommand(
	FileUploadDirectory directory,
	FileUploadImageSize imageSize,
	String memberId,
	String activityId,
	String objectKey
) {

	public FileImageAccessUrlCommand(
		FileUploadDirectory directory,
		FileUploadImageSize imageSize,
		String memberId,
		String objectKey
	) {
		this(directory, imageSize, memberId, null, objectKey);
	}
}
