package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.WithdrawCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
	@Schema(description = "탈퇴를 요청하는 회원의 리프레시 토큰. 모든 탈퇴 요청에 필수입니다.")
	@NotBlank(message = "refresh_token은 필수입니다.")
	String refreshToken
) {
	public WithdrawCommand toCommand(Long memberId, String accessTokenHash, String refreshTokenHash) {
		return new WithdrawCommand(memberId, accessTokenHash, refreshTokenHash);
	}
}
