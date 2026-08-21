package org.sopt.routee.member.internal.controller.dto.response;

import org.sopt.routee.member.internal.service.dto.result.MemberProfileResult;

public record MemberProfileResponse(
	String nickname,
	String profileImageUrl
) {
	public static MemberProfileResponse from(MemberProfileResult result) {
		return new MemberProfileResponse(
			result.nickname(),
			result.profileImageUrl()
		);
	}
}
