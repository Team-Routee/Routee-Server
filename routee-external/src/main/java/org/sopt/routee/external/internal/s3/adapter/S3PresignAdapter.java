package org.sopt.routee.external.internal.s3.adapter;

import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileUploadPresignResult;
import org.sopt.routee.external.internal.s3.config.S3Properties;
import org.sopt.routee.external.internal.s3.exception.FileUploadPresignException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3PresignAdapter implements FileUploadPresignPort {

	private final S3Presigner s3Presigner;
	private final S3Properties properties;

	@Override
	public FileUploadPresignResult generatePutPresignedUrl(FileUploadPresignCommand command) {
		String objectKey = generateStoredObjectKey(
			parseExtension(command.fileName())
		);
		String presignedObjectKey = S3ObjectKeyAssembler.assembleUploadObjectKey(
			command.directory(),
			command.imageSize(),
			command.activityId(),
			objectKey
		);
		String presignedUrl = generatePutPresignedUrl(presignedObjectKey);

		return new FileUploadPresignResult(presignedUrl, objectKey);
	}

	private String generateStoredObjectKey(String extension) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return "%s.%s".formatted(uuid, extension);
	}

	private String parseExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1)
			.toLowerCase(Locale.ROOT);
	}

	private String generatePutPresignedUrl(String objectKey) {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(properties.bucket())
			.key(objectKey)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(properties.presignedUrlExpiryMinutes()))
			.putObjectRequest(putObjectRequest)
			.build();

		try {
			return s3Presigner.presignPutObject(presignRequest)
				.url()
				.toString();
		} catch (RuntimeException e) {
			log.error("Failed to generate S3 presigned URL. bucket={}, objectKey={}", properties.bucket(), objectKey, e);
			throw new FileUploadPresignException(e);
		}
	}
}
