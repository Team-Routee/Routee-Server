package org.sopt.routee.member.internal.service.dto.result;

import java.time.LocalDate;

public record MemberInfoResult(
	String nickname,
	String profileImageUrl,
	LocalDate joinDate,
	long daysSinceJoining,
	int totalActivityCount
) {
}
