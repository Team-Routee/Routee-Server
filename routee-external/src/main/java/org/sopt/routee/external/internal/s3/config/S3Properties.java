package org.sopt.routee.external.internal.s3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
	String bucket,
	String region,
	String endpoint,
	long presignedUrlExpiryMinutes
) {
}
