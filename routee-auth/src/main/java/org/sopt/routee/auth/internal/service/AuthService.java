package org.sopt.routee.auth.internal.service;

import java.time.Duration;

import org.sopt.routee.auth.internal.config.JwtProperties;
import org.sopt.routee.auth.internal.repository.RefreshTokenRepository;
import org.sopt.routee.auth.internal.service.dto.command.LoginCommand;
import org.sopt.routee.auth.internal.exception.InvalidTokenException;
import org.sopt.routee.auth.internal.jwt.JwtParser;
import org.sopt.routee.auth.internal.jwt.JwtProvider;
import org.sopt.routee.auth.internal.jwt.JwtValidator;
import org.sopt.routee.auth.internal.jwt.TokenType;
import org.sopt.routee.auth.internal.service.dto.result.TokenResult;
import org.sopt.routee.external.api.port.OidcVerifyPort;
import org.sopt.routee.member.api.result.TokenClaimsResult;
import org.sopt.routee.member.api.usecase.MemberUseCase;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final String TOKEN_PREFIX = "Bearer";

	private final OidcVerifyPort oidcVerifyPort;
	private final MemberUseCase memberUseCase;
	private final JwtProvider jwtProvider;
	private final JwtValidator jwtValidator;
	private final JwtParser jwtParser;
	private final JwtProperties jwtProperties;
	private final RefreshTokenRepository refreshTokenRepository;

	public TokenResult login(LoginCommand command) {
		String oauthId = oidcVerifyPort.extractSubject(command.provider(), command.idToken());

		TokenClaimsResult tokenClaims = memberUseCase.getTokenResult(oauthId, command.provider());

		return issueTokenPair(tokenClaims.memberId(), tokenClaims.memberRole());
	}

	public TokenResult reissue(String refreshToken) {
		Claims claims = jwtValidator.validate(refreshToken);

		if (jwtParser.extractTokenType(claims) != TokenType.REFRESH) {
			throw new InvalidTokenException();
		}

		if (!refreshTokenRepository.existsByToken(refreshToken)) {
			throw new InvalidTokenException();
		}

		refreshTokenRepository.deleteByToken(refreshToken);

		return issueTokenPair(jwtParser.extractMemberId(claims), jwtParser.extractMemberRole(claims).name());
	}

	private TokenResult issueTokenPair(long memberId, String memberRole) {
		String accessToken = jwtProvider.issueAccessToken(memberId, memberRole);
		String refreshToken = jwtProvider.issueRefreshToken(memberId, memberRole);

		refreshTokenRepository.save(refreshToken, Duration.ofMillis(jwtProperties.refreshTokenExpiryMs()));

		return new TokenResult(accessToken, refreshToken, TOKEN_PREFIX);
	}
}
