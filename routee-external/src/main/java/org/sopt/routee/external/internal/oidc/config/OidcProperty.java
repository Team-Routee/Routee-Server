package org.sopt.routee.external.internal.oidc.config;

public record OidcProperty(
	String jwkSetUri,
	String issuer,
	String clientId
) {
}
