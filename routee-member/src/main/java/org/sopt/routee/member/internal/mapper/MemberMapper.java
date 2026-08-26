package org.sopt.routee.member.internal.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.sopt.routee.activity.api.result.MonthlyActivityDailySummaryResult;
import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;
import org.sopt.routee.member.internal.service.dto.result.ActivitySummaryResult;
import org.sopt.routee.member.internal.service.dto.result.DailySummary;
import org.sopt.routee.member.internal.service.dto.result.MemberInfoResult;
import org.sopt.routee.member.internal.service.dto.result.MemberProfileResult;
import org.sopt.routee.member.internal.service.dto.result.UpdateNicknameResult;
import org.sopt.routee.member.internal.service.dto.result.UpdateProfileImageResult;
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

	public static MemberInfoResult toMemberInfoResult(Member member, String profileImageUrl, ZoneId zoneId) {
		LocalDate joinDate = TimeZoneUtils.toLocalDate(member.getCreatedAt(), zoneId);
		long daysSinceJoining = ChronoUnit.DAYS.between(joinDate, LocalDate.now(zoneId));

		return new MemberInfoResult(
			member.getNickname(),
			profileImageUrl,
			joinDate,
			daysSinceJoining + 1,
			member.getTotalActivityCount()
		);
	}

	public static MemberProfileResult toMemberProfileResult(Member member, String profileImageUrl) {
		return new MemberProfileResult(member.getNickname(), profileImageUrl);
	}

	public static UpdateNicknameResult toUpdateNicknameResult(Member member) {
		return new UpdateNicknameResult(member.getNickname());
	}

	public static UpdateProfileImageResult toUpdateProfileImageResult(String profileImageUrl) {
		return new UpdateProfileImageResult(profileImageUrl);
	}

	public static ActivitySummaryResult toActivitySummaryResult(
		List<MonthlyActivityDailySummaryResult> summaries, int year, int month
	) {
		List<DailySummary> converted = summaries.stream()
			.map(MemberMapper::toDailySummary)
			.toList();

		return new ActivitySummaryResult(converted, year, month);
	}

	private static DailySummary toDailySummary(MonthlyActivityDailySummaryResult summary) {
		return new DailySummary(
			summary.activityDate(),
			summary.totalDurationSeconds(),
			summary.activityCount(),
			summary.coverImageUrl()
		);
	}
}
