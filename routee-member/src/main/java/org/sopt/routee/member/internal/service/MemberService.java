package org.sopt.routee.member.internal.service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import org.sopt.routee.activity.api.result.ActivityDailySummaryResult;
import org.sopt.routee.activity.api.usecase.ActivityUseCase;
import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.external.api.port.OidcVerifyPort;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;
import org.sopt.routee.member.internal.service.dto.result.ActivitySummaryResult;
import org.sopt.routee.member.internal.service.dto.result.MemberInfoResult;
import org.sopt.routee.member.api.result.TokenClaimsResult;
import org.sopt.routee.member.api.usecase.MemberUseCase;
import org.sopt.routee.member.internal.entity.Member;
import org.sopt.routee.member.internal.exception.AlreadyRegisteredMemberException;
import org.sopt.routee.member.internal.exception.MemberNotFoundException;
import org.sopt.routee.member.internal.mapper.MemberMapper;
import org.sopt.routee.member.internal.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

	private final OidcVerifyPort oidcVerifyPort;
	private final ActivityUseCase activityUseCase;
	private final MemberRepository memberRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional(readOnly = true)
	public TokenClaimsResult getTokenResult(String oauthId, OAuthProvider oauthProvider) {
		Member member = memberRepository.findByOauthIdAndOauthProvider(oauthId, oauthProvider)
			.orElseThrow(MemberNotFoundException::new);

		return MemberMapper.toTokenClaimsResult(member);
	}

	@Transactional
	public void register(RegisterCommand command) {
		String oauthId = oidcVerifyPort.extractSubject(command.provider(), command.idToken());

		if (memberRepository.existsByOauthIdAndOauthProvider(oauthId, command.provider())) {
			throw new AlreadyRegisteredMemberException();
		}
		memberRepository.save(MemberMapper.toEntity(command, oauthId));
	}

	@Transactional
	public void withdraw(long memberId, String accessTokenHash, String refreshTokenHash) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(MemberNotFoundException::new);

		memberRepository.delete(member);

		applicationEventPublisher.publishEvent(new MemberWithdrawnEvent(memberId, accessTokenHash, refreshTokenHash));
	}

	@Transactional(readOnly = true)
	public MemberInfoResult getMemberInfo(long memberId, ZoneId zoneId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(MemberNotFoundException::new);

		return MemberMapper.toMemberInfoResult(member, zoneId);
	}

	@Transactional(readOnly = true)
	public ActivitySummaryResult getActivitySummary(long memberId, int year, int month) {
		if (!memberRepository.existsById(memberId)) {
			throw new MemberNotFoundException();
		}

		List<ActivityDailySummaryResult> summaries =
			activityUseCase.getMonthlySummaries(memberId, YearMonth.of(year, month));

		return MemberMapper.toActivitySummaryResult(summaries, year, month);
	}
}
