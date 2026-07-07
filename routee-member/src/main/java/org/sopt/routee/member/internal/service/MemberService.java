package org.sopt.routee.member.internal.service;

import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.external.api.port.OidcVerifyPort;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;
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
	public void withdraw(long memberId, String accessTokenWithBearer, String refreshToken) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(MemberNotFoundException::new);

		memberRepository.delete(member);

		applicationEventPublisher.publishEvent(new MemberWithdrawnEvent(memberId, accessTokenWithBearer, refreshToken));
	}
}
