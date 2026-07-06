package org.sopt.routee.auth.internal.jwt;

import org.sopt.routee.auth.internal.exception.InvalidTokenException;
import org.sopt.routee.member.api.type.MemberRole;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;

@Component
public class JwtParser {

	public Long extractMemberId(Claims claims) {
		return Long.parseLong(claims.getSubject());
	}

	public TokenType extractTokenType(Claims claims) {
		String type = claims.get(JwtClaimKeys.TOKEN_TYPE.getKey(), String.class);
		try {
			return TokenType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new InvalidTokenException();
		}
	}

	public MemberRole extractMemberRole(Claims claims) {
		String role = claims.get(JwtClaimKeys.MEMBER_ROLE.getKey(), String.class);
		try {
			return MemberRole.valueOf(role.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new InvalidTokenException();
		}
	}
}
