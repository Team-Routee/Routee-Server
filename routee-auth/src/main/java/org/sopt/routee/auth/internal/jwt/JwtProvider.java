package org.sopt.routee.auth.internal.jwt;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.sopt.routee.auth.internal.config.JwtProperties;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	private final SecretKey secretKey;
	private final JwtProperties jwtProperties;

	public String issueAccessToken(long memberId, String role) {
		return issueToken(TokenType.ACCESS, memberId, role);
	}

	public String issueRefreshToken(long memberId, String role) {
		return issueToken(TokenType.REFRESH, memberId, role);
	}

	private String issueToken(TokenType tokenType, long memberId, String role) {
		Instant now = Instant.now();

		Instant expiration = switch (tokenType) {
			case ACCESS -> now.plusMillis(jwtProperties.accessTokenExpiryMs());
			case REFRESH -> now.plusMillis(jwtProperties.refreshTokenExpiryMs());
		};

		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.issuer(jwtProperties.issuer())
			.claim(JwtClaimKeys.TOKEN_TYPE.getKey(), tokenType.value())
			.claim(JwtClaimKeys.MEMBER_ROLE.getKey(), role)
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiration))
			.signWith(secretKey)
			.compact();
	}
}
