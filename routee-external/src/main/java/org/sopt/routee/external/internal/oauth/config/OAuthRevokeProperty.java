package org.sopt.routee.external.internal.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.providers.apple")
public record OAuthRevokeProperty(
	String tokenUri,
	String revokeUri,
	String audience,
	String clientId,
	String teamId,
	String keyId,
	String privateKey
) {
}
