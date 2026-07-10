package org.sopt.routee.member.internal.controller.dto.response;

import java.time.LocalDate;

import org.sopt.routee.member.internal.service.dto.result.MemberInfoResult;

public record MemberInfoResponse(
	String nickname,
	String profileImageUrl,
	LocalDate joinDate,
	long daysSinceJoining,
	int totalActivityCount
) {
	public static MemberInfoResponse from(MemberInfoResult result) {
		return new MemberInfoResponse(
			result.nickname(),
			result.profileImageUrl(),
			result.joinDate(),
			result.daysSinceJoining(),
			result.totalActivityCount()
		);
	}
}
