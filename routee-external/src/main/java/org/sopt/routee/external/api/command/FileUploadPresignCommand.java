package org.sopt.routee.external.api.command;

import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageType;

public record FileUploadPresignCommand(
	FileUploadDirectory directory,
	FileUploadImageType imageType,
	String resourceId,
	String fileName
) {
}
