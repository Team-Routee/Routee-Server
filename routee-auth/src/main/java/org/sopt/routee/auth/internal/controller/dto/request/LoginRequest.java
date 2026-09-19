package org.sopt.routee.auth.internal.controller.dto.request;

import org.sopt.routee.auth.internal.service.dto.command.LoginCommand;
import org.sopt.routee.external.api.type.OAuthProvider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
	@NotNull(message = "provider는 필수입니다.")
	OAuthProvider provider,

	@NotBlank(message = "id_token은 필수입니다.")
	String idToken,

	@Schema(description = "Apple 로그인 시 함께 전달받은 인가 코드. Apple refresh_token 발급에 사용됩니다. "
		+ "아직 저장된 Apple refresh_token이 없는 회원은 필수이며, 없으면 로그인이 실패합니다. "
		+ "이미 발급받아 저장된 회원이라면 전달하지 않아도 되고 전달되어도 무시됩니다. Apple 외 소셜 로그인 회원은 필요하지 않습니다.")
	String authorizationCode
) {
	public LoginCommand toCommand() {
		return new LoginCommand(provider, idToken, authorizationCode);
	}
}
