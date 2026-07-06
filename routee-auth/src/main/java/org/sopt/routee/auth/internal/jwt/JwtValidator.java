package org.sopt.routee.auth.internal.jwt;

import javax.crypto.SecretKey;

import org.sopt.routee.auth.internal.config.JwtProperties;
import org.sopt.routee.auth.internal.exception.InvalidTokenException;
import org.sopt.routee.auth.internal.exception.TokenExpiredException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtValidator {

	private final SecretKey jwtSecretKey;
	private final JwtProperties jwtProperties;

	public Claims validate(String token) {
		try {
			return Jwts.parser()
				.verifyWith(jwtSecretKey)
				.requireIssuer(jwtProperties.issuer())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			throw new TokenExpiredException();
		} catch (JwtException e) {
			throw new InvalidTokenException();
		}
	}
}
