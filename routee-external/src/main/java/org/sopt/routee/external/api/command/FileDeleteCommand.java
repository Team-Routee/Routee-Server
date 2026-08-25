package org.sopt.routee.external.api.command;

import org.sopt.routee.external.api.type.FileUploadDirectory;

public record FileDeleteCommand(
	FileUploadDirectory directory,
	String memberId,
	String activityId,
	String objectKey
) {
}
