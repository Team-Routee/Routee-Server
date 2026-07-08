package org.sopt.routee.member.internal.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;
import org.sopt.routee.member.internal.service.dto.result.MemberInfoResult;
import org.sopt.routee.member.api.result.TokenClaimsResult;
import org.sopt.routee.member.api.type.MemberRole;
import org.sopt.routee.member.internal.entity.Member;
import org.sopt.routee.util.TimeZoneUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

	public static Member toEntity(RegisterCommand command, String oauthId) {
		return Member.builder()
			.nickname(command.nickname())
			.oauthId(oauthId)
			.oauthProvider(command.provider())
			.role(MemberRole.ROLE_USER)
			.totalActivityCount(0)
			.build();
	}

	public static TokenClaimsResult toTokenClaimsResult(Member member) {
		return new TokenClaimsResult(member.getId(), member.getRole().name());
	}

	public static MemberInfoResult toMemberInfoResult(Member member, ZoneId zoneId) {
		LocalDate joinDate = TimeZoneUtils.toLocalDate(member.getCreatedAt(), zoneId);
		long daysSinceJoining = ChronoUnit.DAYS.between(joinDate, LocalDate.now(zoneId));

		return new MemberInfoResult(
			member.getNickname(),
			member.getProfileImageUrl(),
			joinDate,
			daysSinceJoining + 1,
			member.getTotalActivityCount()
		);
	}
}
