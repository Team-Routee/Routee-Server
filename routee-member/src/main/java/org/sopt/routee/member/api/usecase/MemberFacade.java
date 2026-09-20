package org.sopt.routee.member.api.usecase;

import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.api.result.TokenClaimsResult;
import org.sopt.routee.member.internal.service.MemberService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberFacade implements MemberUseCase {
	private final MemberService memberService;

	public TokenClaimsResult getTokenResult(String oauthId, OAuthProvider oauthProvider, String authorizationCode) {
		return memberService.getTokenResult(oauthId, oauthProvider, authorizationCode);
	}

	public boolean existsById(long memberId) {
		return memberService.existsById(memberId);
	}
}