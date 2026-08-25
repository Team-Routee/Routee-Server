package org.sopt.routee.external.internal.s3.adapter;

import java.util.Arrays;
import java.util.List;

import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.command.FileDeleteDirectoryCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;
import org.sopt.routee.external.internal.s3.config.S3Properties;
import org.sopt.routee.external.internal.s3.exception.FileDeleteException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Component
@RequiredArgsConstructor
public class S3FileDeleteAdapter implements FileDeletePort {

	private final S3Client s3Client;
	private final S3Properties properties;

	@Override
	public void deleteImage(FileDeleteCommand command) {
		List<ObjectIdentifier> objectIdentifiers = resolveObjectKeys(command).stream()
			.map(objectKey -> ObjectIdentifier.builder().key(objectKey).build())
			.toList();

		deleteObjects(objectIdentifiers);
	}

	@Override
	public void deleteDirectory(FileDeleteDirectoryCommand command) {
		String prefix = S3ObjectKeyAssembler.assembleActivityDirectoryPrefix(command.memberId(), command.activityId());

		String continuationToken = null;
		do {
			ListObjectsV2Response listResponse = listObjects(prefix, continuationToken);

			List<ObjectIdentifier> objectIdentifiers = listResponse.contents().stream()
				.map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
				.toList();

			if (!objectIdentifiers.isEmpty()) {
				deleteObjects(objectIdentifiers);
			}

			continuationToken = listResponse.isTruncated() ? listResponse.nextContinuationToken() : null;
		} while (continuationToken != null);
	}

	private ListObjectsV2Response listObjects(String prefix, String continuationToken) {
		ListObjectsV2Request request = ListObjectsV2Request.builder()
			.bucket(properties.bucket())
			.prefix(prefix)
			.continuationToken(continuationToken)
			.build();

		try {
			return s3Client.listObjectsV2(request);
		} catch (RuntimeException e) {
			throw new FileDeleteException(e);
		}
	}

	private void deleteObjects(List<ObjectIdentifier> objectIdentifiers) {
		DeleteObjectsRequest request = DeleteObjectsRequest.builder()
			.bucket(properties.bucket())
			.delete(Delete.builder().objects(objectIdentifiers).build())
			.build();

		DeleteObjectsResponse response;
		try {
			response = s3Client.deleteObjects(request);
		} catch (RuntimeException e) {
			throw new FileDeleteException(e);
		}

		if (response.hasErrors()) {
			throw new FileDeleteException();
		}
	}

	private List<String> resolveObjectKeys(FileDeleteCommand command) {
		if (command.directory() != FileUploadDirectory.TIMELINE) {
			return List.of(S3ObjectKeyAssembler.assemble(
				command.directory(), null, command.memberId(), command.activityId(), command.objectKey()
			));
		}

		return Arrays.stream(FileUploadImageSize.values())
			.map(imageSize -> S3ObjectKeyAssembler.assemble(
				command.directory(), imageSize, command.memberId(), command.activityId(), command.objectKey()
			))
			.toList();
	}
}
