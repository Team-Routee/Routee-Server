package org.sopt.routee.external.internal.oauth.adapter;

import org.sopt.routee.external.api.port.OAuthRevokePort;
import org.sopt.routee.external.internal.oauth.config.OAuthRevokeProperty;
import org.sopt.routee.external.internal.oauth.exception.OAuthRevokeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AppleOAuthRevokeAdapter implements OAuthRevokePort {

	private static final String REFRESH_TOKEN_HINT = "refresh_token";

	private final RestClient restClient;
	private final OAuthRevokeProperty property;
	private final AppleClientSecretGenerator clientSecretGenerator;

	@Override
	public void revoke(String authorizationCode) {
		OAuthTokenResponse token = exchangeAuthorizationCode(authorizationCode);

		if (!StringUtils.hasText(token.refreshToken())) {
			throw new OAuthRevokeException();
		}

		requestRevoke(token.refreshToken(), REFRESH_TOKEN_HINT);
	}

	private OAuthTokenResponse exchangeAuthorizationCode(String authorizationCode) {
		MultiValueMap<String, String> form = credentialForm();

		form.add("grant_type", "authorization_code");
		form.add("code", authorizationCode);

		OAuthTokenResponse response = post(property.tokenUri(), form, OAuthTokenResponse.class);

		if (response == null) {
			throw new OAuthRevokeException();
		}

		return response;
	}

	private void requestRevoke(String token, String tokenTypeHint) {
		MultiValueMap<String, String> form = credentialForm();
		form.add("token", token);
		form.add("token_type_hint", tokenTypeHint);

		post(property.revokeUri(), form, Void.class);
	}

	private <T> T post(String uri, MultiValueMap<String, String> form, Class<T> responseType) {
		try {
			return restClient.post()
				.uri(uri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.body(responseType);
		} catch (RestClientException e) {
			throw new OAuthRevokeException(e);
		}
	}

	private MultiValueMap<String, String> credentialForm() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

		form.add("client_id", property.clientId());
		form.add("client_secret", clientSecretGenerator.generate());

		return form;
	}
}
