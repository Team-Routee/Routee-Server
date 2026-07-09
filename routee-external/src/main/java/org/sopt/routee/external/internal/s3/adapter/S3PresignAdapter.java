package org.sopt.routee.external.internal.s3.adapter;

import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

import org.sopt.routee.external.api.command.FileUploadGetPresignCommand;
import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileUploadPresignResult;
import org.sopt.routee.external.internal.s3.config.S3Properties;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3PresignAdapter implements FileUploadPresignPort {

	private final S3Presigner s3Presigner;
	private final S3Properties properties;

	@Override
	public FileUploadPresignResult generatePutPresignedUrl(FileUploadPresignCommand command) {
		String objectKey = generateStoredObjectKey(
			command.resourceId(),
			parseExtension(command.fileName())
		);
		String presignedObjectKey = assemblePresignedObjectKey(
			command.directory().path(),
			command.imageSize().path(),
			objectKey
		);
		String presignedUrl = generatePutPresignedUrl(presignedObjectKey);

		return new FileUploadPresignResult(presignedUrl, objectKey);
	}

	@Override
	public String generateGetPresignedUrl(FileUploadGetPresignCommand command) {
		String presignedObjectKey = assemblePresignedObjectKey(
			command.directory().path(),
			command.imageSize().path(),
			command.objectKey()
		);

		return generateGetPresignedUrl(presignedObjectKey);
	}

	private String generateStoredObjectKey(String resourceId, String extension) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return "%s/%s.%s"
			.formatted(resourceId, uuid, extension);
	}

	private String parseExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1)
			.toLowerCase(Locale.ROOT);
	}

	private String assemblePresignedObjectKey(String directoryPath, String imageSizePath, String storedObjectKey) {
		return "%s/%s/%s".formatted(directoryPath, imageSizePath, storedObjectKey);
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

		return s3Presigner.presignPutObject(presignRequest)
			.url()
			.toString();
	}

	private String generateGetPresignedUrl(String objectKey) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			.bucket(properties.bucket())
			.key(objectKey)
			.build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(properties.presignedUrlExpiryMinutes()))
			.getObjectRequest(getObjectRequest)
			.build();

		return s3Presigner.presignGetObject(presignRequest)
			.url()
			.toString();
	}
}
