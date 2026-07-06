package org.sopt.routee.member.api.result;

public record TokenClaimsResult(
	long memberId,
	String memberRole
) {
}
