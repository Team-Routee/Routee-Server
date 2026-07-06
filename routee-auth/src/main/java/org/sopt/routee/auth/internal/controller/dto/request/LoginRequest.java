package org.sopt.routee.auth.internal.controller.dto.request;

import org.sopt.routee.auth.internal.service.dto.command.LoginCommand;
import org.sopt.routee.external.api.type.OAuthProvider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
	@NotNull(message = "provider는 필수입니다.")
	OAuthProvider provider,

	@NotBlank(message = "id_token은 필수입니다.")
	String idToken
) {
	public LoginCommand toCommand() {
		return new LoginCommand(provider, idToken);
	}
}
