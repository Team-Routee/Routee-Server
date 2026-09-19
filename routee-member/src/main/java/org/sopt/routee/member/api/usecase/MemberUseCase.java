package org.sopt.routee.member.api.usecase;

import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.api.result.TokenClaimsResult;

public interface MemberUseCase {

	TokenClaimsResult getTokenResult(String oauthId, OAuthProvider oauthProvider, String authorizationCode);

	boolean existsById(long memberId);

}
