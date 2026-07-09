package org.sopt.routee.external.internal.s3.adapter;

import org.sopt.routee.external.api.command.FileImageAccessUrlCommand;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.result.FileImageAccessUrlResult;
import org.sopt.routee.external.internal.s3.config.S3Properties;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3FileImageAccessUrlAdapter implements FileImageAccessUrlPort {

	private final S3Properties properties;

	@Override
	public FileImageAccessUrlResult generateImageUrl(FileImageAccessUrlCommand command) {
		String objectKey = S3ObjectKeyAssembler.assemble(
			command.directory(),
			command.imageSize(),
			command.activityId(),
			command.objectKey()
		);
		return new FileImageAccessUrlResult("%s/%s".formatted(normalizeEndpoint(properties.endpoint()), objectKey));
	}

	private String normalizeEndpoint(String endpoint) {
		return endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
	}
}
