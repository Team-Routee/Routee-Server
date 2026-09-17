package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.WithdrawCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
	@NotBlank(message = "refresh_token은 필수입니다.")
	String refreshToken,

	@Schema(description = "탈퇴 시점에 재인증하여 발급받은 Apple 인가 코드. Apple 계정 연동 해제에 사용되며, Apple 로그인 회원만 필요합니다.")
	String authorizationCode
) {
	public WithdrawCommand toCommand(Long memberId, String accessTokenHash, String refreshTokenHash) {
		return new WithdrawCommand(memberId, accessTokenHash, refreshTokenHash, authorizationCode);
	}
}
