package org.sopt.routee.external.internal.oauth.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;

record OAuthTokenResponse(
	@JsonProperty("refresh_token") String refreshToken
) {
}
