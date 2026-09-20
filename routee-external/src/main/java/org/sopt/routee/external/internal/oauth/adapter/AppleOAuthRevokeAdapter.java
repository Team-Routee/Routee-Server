package org.sopt.routee.external.internal.oauth.adapter;

import java.util.Set;

import org.sopt.routee.external.api.port.OAuthRevokePort;
import org.sopt.routee.external.internal.oauth.exception.OAuthRevokeException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class AppleOAuthRevokeAdapter implements OAuthRevokePort {

	private static final String REFRESH_TOKEN_HINT = "refresh_token";
	private static final Set<String> ALREADY_INVALID_ERRORS = Set.of("invalid_token", "invalid_grant");

	private final AppleOAuthFormClient client;
	private final ObjectMapper objectMapper;

	@Override
	public void revoke(String refreshToken) {
		MultiValueMap<String, String> form = client.credentialForm();

		form.add("token", refreshToken);
		form.add("token_type_hint", REFRESH_TOKEN_HINT);

		try {
			client.post(client.revokeUri(), form, Void.class);
		} catch (HttpClientErrorException e) {
			if (isAlreadyInvalid(e)) {
				log.info("Apple OAuth token already invalid/revoked. Treating as success.");
				return;
			}
			throw new OAuthRevokeException(e);
		} catch (RestClientException e) {
			throw new OAuthRevokeException(e);
		}
	}

	private boolean isAlreadyInvalid(HttpClientErrorException e) {
		try {
			AppleOAuthErrorResponse errorResponse =
				objectMapper.readValue(e.getResponseBodyAsString(), AppleOAuthErrorResponse.class);
			return ALREADY_INVALID_ERRORS.contains(errorResponse.error());
		} catch (JacksonException parseException) {
			return false;
		}
	}
}
