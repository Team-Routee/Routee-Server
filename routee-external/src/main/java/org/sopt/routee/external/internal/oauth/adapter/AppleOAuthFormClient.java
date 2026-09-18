package org.sopt.routee.external.internal.oauth.adapter;

import org.sopt.routee.external.internal.oauth.config.OAuthRevokeProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AppleOAuthFormClient {

	private final RestClient restClient;
	private final OAuthRevokeProperty property;
	private final AppleClientSecretGenerator clientSecretGenerator;

	MultiValueMap<String, String> credentialForm() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

		form.add("client_id", property.clientId());
		form.add("client_secret", clientSecretGenerator.generate());

		return form;
	}

	<T> T post(String uri, MultiValueMap<String, String> form, Class<T> responseType) {
		return restClient.post()
			.uri(uri)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(form)
			.retrieve()
			.body(responseType);
	}

	String tokenUri() {
		return property.tokenUri();
	}

	String revokeUri() {
		return property.revokeUri();
	}
}
