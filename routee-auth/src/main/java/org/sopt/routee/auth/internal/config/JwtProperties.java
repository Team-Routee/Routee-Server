package org.sopt.routee.auth.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	String secret,
	String issuer,
	long accessTokenExpiryMs,
	long refreshTokenExpiryMs
) {
}
