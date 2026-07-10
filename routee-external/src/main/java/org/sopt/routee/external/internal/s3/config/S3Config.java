package org.sopt.routee.external.internal.s3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
class S3Config {

	@Bean
	S3Presigner s3Presigner(S3Properties properties) {
		return S3Presigner.builder()
			.region(Region.of(properties.region()))
			.build();
	}
}
