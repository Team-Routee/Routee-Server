package org.sopt.routee.member.api.event;

public record MemberWithdrawEvent(
	Long memberId,
	String accessTokenWithBearer,
	String refreshToken
) {
}