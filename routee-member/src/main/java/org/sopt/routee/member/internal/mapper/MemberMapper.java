package org.sopt.routee.member.internal.mapper;

import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;
import org.sopt.routee.member.api.result.TokenClaimsResult;
import org.sopt.routee.member.api.type.MemberRole;
import org.sopt.routee.member.internal.entity.Member;

public class MemberMapper {

	public static Member toEntity(RegisterCommand command, String oauthId) {
		return Member.builder()
			.nickname(command.nickname())
			.oauthId(oauthId)
			.oauthProvider(command.provider())
			.role(MemberRole.ROLE_USER)
			.currentStreak(0)
			.totalActivityCount(0)
			.build();
	}

	public static TokenClaimsResult toTokenClaimsResult(Member member) {
		return new TokenClaimsResult(member.getId(), member.getRole().name());
	}
}