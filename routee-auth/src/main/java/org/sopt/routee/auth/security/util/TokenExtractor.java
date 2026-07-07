package org.sopt.routee.auth.security.util;

import org.sopt.routee.auth.internal.exception.InvalidTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenExtractor {

	private static final String BEARER_PREFIX = "Bearer ";

	public static String extract(String accessTokenWithBearer) {
		if (!StringUtils.hasText(accessTokenWithBearer)) {
			return null;
		}
		if (!accessTokenWithBearer.startsWith(BEARER_PREFIX)) {
			throw new InvalidTokenException();
		}
		return accessTokenWithBearer.substring(BEARER_PREFIX.length());
	}
}
