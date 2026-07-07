package org.sopt.routee.member.api.event;

public record MemberWithdrawnEvent(
	Long memberId,
	String accessTokenHash,
	String refreshTokenHash
) {
}
