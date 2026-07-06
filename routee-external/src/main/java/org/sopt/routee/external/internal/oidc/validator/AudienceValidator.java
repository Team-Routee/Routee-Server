package org.sopt.routee.external.internal.oidc.validator;

import org.sopt.routee.external.internal.oidc.code.ErrorCode;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

	private static final OAuth2TokenValidatorResult INVALID_AUDIENCE =
		OAuth2TokenValidatorResult.failure(
			new OAuth2Error(ErrorCode.INVALID_AUDIENCE.toString(),
				ErrorCode.INVALID_AUDIENCE.getMessage(), null)
		);

	private final String clientId;

	public OAuth2TokenValidatorResult validate(Jwt token) {
		return token.getAudience().contains(clientId)
			? OAuth2TokenValidatorResult.success()
			: INVALID_AUDIENCE;
	}
}
