package org.sopt.routee.external.internal.s3.adapter;

import java.time.Duration;

import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.internal.s3.config.S3Properties;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3PresignAdapter implements FileUploadPresignPort {

	private final S3Presigner s3Presigner;
	private final S3Properties properties;

	@Override
	public String generatePutPresignedUrl(String objectKey) {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(properties.bucket())
			.key(objectKey)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(properties.presignedUrlExpiryMinutes()))
			.putObjectRequest(putObjectRequest)
			.build();

		return s3Presigner.presignPutObject(presignRequest)
			.url()
			.toString();
	}
}
