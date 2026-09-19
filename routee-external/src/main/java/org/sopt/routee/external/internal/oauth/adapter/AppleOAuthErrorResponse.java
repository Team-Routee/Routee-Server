package org.sopt.routee.external.internal.oauth.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;

record AppleOAuthErrorResponse(
	@JsonProperty("error") String error
) {
}
