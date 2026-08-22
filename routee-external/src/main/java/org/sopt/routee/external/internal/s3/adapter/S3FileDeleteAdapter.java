package org.sopt.routee.external.internal.s3.adapter;

import java.util.Arrays;
import java.util.List;

import org.sopt.routee.external.api.command.FileDeleteCommand;
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
				command.directory(), null, command.activityId(), command.objectKey()
			));
		}

		return Arrays.stream(FileUploadImageSize.values())
			.map(imageSize -> S3ObjectKeyAssembler.assemble(
				command.directory(), imageSize, command.activityId(), command.objectKey()
			))
			.toList();
	}
}
