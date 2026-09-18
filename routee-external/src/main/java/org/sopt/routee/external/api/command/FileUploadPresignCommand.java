package org.sopt.routee.external.api.command;

import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;

public record FileUploadPresignCommand(
	FileUploadDirectory directory,
	FileUploadImageSize imageSize,
	String memberId,
	String activityId,
	String fileName
) {

	public FileUploadPresignCommand(
		FileUploadDirectory directory,
		FileUploadImageSize imageSize,
		String memberId,
		String fileName
	) {
		this(directory, imageSize, memberId, null, fileName);
	}
}
