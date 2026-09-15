package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.WithdrawCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
	@NotBlank(message = "refresh_token은 필수입니다.")
	String refreshToken,

	@Schema(description = "탈퇴 시점에 재인증하여 발급받은 소셜 로그인 인가 코드. provider와의 연동 해제에 사용됩니다.")
	@NotBlank(message = "authorization_code는 필수입니다.")
	String authorizationCode
) {
	public WithdrawCommand toCommand(Long memberId, String accessTokenHash, String refreshTokenHash) {
		return new WithdrawCommand(memberId, accessTokenHash, refreshTokenHash, authorizationCode);
	}
}
