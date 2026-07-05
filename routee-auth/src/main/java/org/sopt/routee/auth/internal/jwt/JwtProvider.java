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

	private static final String TOKEN_TYPE = "type";
	private static final String MEMBER_TYPE = "role";

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
			.claim(TOKEN_TYPE, tokenType.value())
			.claim(MEMBER_TYPE, role)
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiration))
			.signWith(secretKey)
			.compact();
	}
}
