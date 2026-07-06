package org.sopt.routee.external.internal.oidc.adapter;

import org.sopt.routee.external.internal.oidc.decoder.OidcJwtDecoderProvider;
import org.sopt.routee.external.internal.oidc.exception.ExpiredOidcTokenException;
import org.sopt.routee.external.internal.oidc.exception.InvalidOidcClaimsException;
import org.sopt.routee.external.internal.oidc.exception.InvalidOidcTokenException;
import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.external.api.port.OidcVerifyPort;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OidcJwtDecoderAdapter implements OidcVerifyPort {

	private final OidcJwtDecoderProvider decoderProvider;

	@Override
	public String extractSubject(OAuthProvider provider, String idToken) {
		try {
			return decoderProvider.get(provider.name().toLowerCase())
				.decode(idToken)
				.getSubject();
		} catch (JwtValidationException e) {
			boolean isExpired = e.getErrors().stream()
				.anyMatch(error ->
					error.getDescription() != null
						&& error.getDescription().contains("expired"));

			throw isExpired
				? new ExpiredOidcTokenException()
				: new InvalidOidcClaimsException();
		} catch (JwtException e) {
			throw new InvalidOidcTokenException(e);
		}
	}
}
