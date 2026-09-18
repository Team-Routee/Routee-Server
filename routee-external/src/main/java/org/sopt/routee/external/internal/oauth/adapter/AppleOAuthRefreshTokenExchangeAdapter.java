package org.sopt.routee.external.internal.oauth.adapter;

import org.sopt.routee.external.api.port.OAuthRefreshTokenExchangePort;
import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.external.internal.oauth.exception.OAuthRefreshTokenExchangeException;
import org.sopt.routee.external.internal.oidc.exception.UnsupportedOidcProviderException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AppleOAuthRefreshTokenExchangeAdapter implements OAuthRefreshTokenExchangePort {

	private final AppleOAuthFormClient client;

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
		} catch (RestClientException e) {
			throw new OAuthRefreshTokenExchangeException(e);
		}
	}
}
