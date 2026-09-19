package org.sopt.routee.external.internal.oauth.adapter;

import org.sopt.routee.external.api.exception.OAuthAuthorizationCodeExpiredException;
import org.sopt.routee.external.api.port.OAuthRefreshTokenExchangePort;
import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.external.internal.oauth.exception.OAuthRefreshTokenExchangeException;
import org.sopt.routee.external.internal.oidc.exception.UnsupportedOidcProviderException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AppleOAuthRefreshTokenExchangeAdapter implements OAuthRefreshTokenExchangePort {

	private static final String INVALID_GRANT = "invalid_grant";

	private final AppleOAuthFormClient client;
	private final ObjectMapper objectMapper;

	@Override
	public String exchangeForRefreshToken(OAuthProvider provider, String authorizationCode) {
		if (provider != OAuthProvider.APPLE) {
			throw new UnsupportedOidcProviderException();
		}

		MultiValueMap<String, String> form = client.credentialForm();

		form.add("grant_type", "authorization_code");
		form.add("code", authorizationCode);

		try {
			OAuthTokenResponse response = client.post(client.tokenUri(), form, OAuthTokenResponse.class);

			if (response == null || !StringUtils.hasText(response.refreshToken())) {
				throw new OAuthRefreshTokenExchangeException();
			}

			return response.refreshToken();
		} catch (HttpClientErrorException e) {
			if (isInvalidGrant(e)) {
				throw new OAuthAuthorizationCodeExpiredException(e);
			}
			throw new OAuthRefreshTokenExchangeException(e);
		} catch (RestClientException e) {
			throw new OAuthRefreshTokenExchangeException(e);
		}
	}

	private boolean isInvalidGrant(HttpClientErrorException e) {
		try {
			AppleOAuthErrorResponse errorResponse =
				objectMapper.readValue(e.getResponseBodyAsString(), AppleOAuthErrorResponse.class);
			return INVALID_GRANT.equals(errorResponse.error());
		} catch (JacksonException parseException) {
			return false;
		}
	}
}
