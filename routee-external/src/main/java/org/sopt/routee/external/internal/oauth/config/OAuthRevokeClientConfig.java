package org.sopt.routee.external.internal.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class OAuthRevokeClientConfig {

	@Bean
	RestClient oauthRevokeRestClient() {
		return RestClient.create();
	}
}
