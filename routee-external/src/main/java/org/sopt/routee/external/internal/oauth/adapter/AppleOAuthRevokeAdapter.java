package org.sopt.routee.external.internal.oauth.adapter;

import org.sopt.routee.external.api.port.OAuthRevokePort;
import org.sopt.routee.external.internal.oauth.exception.OAuthRevokeException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AppleOAuthRevokeAdapter implements OAuthRevokePort {

	private static final String REFRESH_TOKEN_HINT = "refresh_token";

	private final AppleOAuthFormClient client;

	@Override
	public void revoke(String refreshToken) {
		MultiValueMap<String, String> form = client.credentialForm();

		form.add("token", refreshToken);
		form.add("token_type_hint", REFRESH_TOKEN_HINT);

		try {
			client.post(client.revokeUri(), form, Void.class);
		} catch (RestClientException e) {
			throw new OAuthRevokeException(e);
		}
	}
}
