package org.sopt.routee.member.api.event;

public record MemberWithdrawnEvent(
	Long memberId,
	String accessTokenWithBearer,
	String refreshToken
) {
}