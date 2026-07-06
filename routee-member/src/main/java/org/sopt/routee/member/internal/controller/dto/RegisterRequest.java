package org.sopt.routee.member.internal.controller.dto;

import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
	@NotNull
	OAuthProvider provider,
	@NotBlank
	String idToken,
	@NotBlank
	String nickname
) {
	public RegisterCommand toCommand(){
		return new RegisterCommand(provider, idToken, nickname);
	}
}
