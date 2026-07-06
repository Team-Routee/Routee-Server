package org.sopt.routee.external.internal.oidc.config;

import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oidc")
public record OidcProperties(
	Map<String, OidcProperty> properties
) {
	public Optional<OidcProperty> get(String provider) {
		return Optional.ofNullable(properties.get(provider));
	}
}
