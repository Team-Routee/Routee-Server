package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.WithdrawCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
	@Schema(description = "탈퇴를 요청하는 회원의 리프레시 토큰. 모든 탈퇴 요청에 필수입니다.")
	@NotBlank(message = "refresh_token은 필수입니다.")
	String refreshToken,

	@Schema(description = "탈퇴 시점에 재인증하여 발급받은 Apple 인가 코드. Apple 계정 연동 해제에 사용되며, Apple 로그인 회원만 필요합니다. "
		+ "그 외 소셜 로그인 회원은 전달하지 않아도 됩니다.")
	String authorizationCode
) {
	public WithdrawCommand toCommand(Long memberId, String accessTokenHash, String refreshTokenHash) {
		return new WithdrawCommand(memberId, accessTokenHash, refreshTokenHash, authorizationCode);
	}
}
