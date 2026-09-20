package org.sopt.routee.member.internal.service.dto.command;

public record WithdrawCommand(
	Long memberId,
	String accessTokenHash,
	String refreshTokenHash
) {
}
