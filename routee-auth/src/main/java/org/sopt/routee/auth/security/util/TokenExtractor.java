package org.sopt.routee.auth.security.util;

import org.sopt.routee.auth.internal.exception.InvalidTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class TokenExtractor {

	private static final String BEARER_PREFIX = "Bearer ";

	private TokenExtractor() {}

	public static String extract(HttpServletRequest request) {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (!StringUtils.hasText(header)) {
			return null;
		}
		if (!header.startsWith(BEARER_PREFIX)) {
			throw new InvalidTokenException();
		}
		return header.substring(BEARER_PREFIX.length());
	}
}
