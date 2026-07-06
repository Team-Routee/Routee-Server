package org.sopt.routee.external.internal.oidc.decoder;

import java.util.Map;

import org.sopt.routee.external.internal.oidc.validator.AudienceValidator;
import org.sopt.routee.external.internal.oidc.config.OidcProperties;
import org.sopt.routee.external.internal.oidc.config.OidcProperty;
import org.sopt.routee.external.internal.oidc.exception.UnsupportedOidcProviderException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class OidcJwtDecoderProvider {

	private final Map<String, JwtDecoder> decoders;

	public OidcJwtDecoderProvider(OidcProperties properties) {
		this.decoders = properties.properties().entrySet().stream()
			.collect(java.util.stream.Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				entry -> createDecoder(entry.getValue())
			));
	}

	public JwtDecoder get(String provider) {
		JwtDecoder decoder = decoders.get(provider);

		if (decoder == null) {
			throw new UnsupportedOidcProviderException();
		}

		return decoder;
	}

	private JwtDecoder createDecoder(OidcProperty property) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(property.jwkSetUri())
			.build();

		decoder.setJwtValidator(
			new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(property.issuer()),
				new AudienceValidator(property.clientId())
			)
		);

		return decoder;
	}
}
