package org.sopt.routee.external.api.result;

public record FileUploadPresignResult(
	String presignedUrl,
	String objectKey
) {
}
