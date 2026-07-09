package org.sopt.routee.util;

import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenExtractor {

	private static final String BEARER_PREFIX = "Bearer ";

	public static String extract(String accessTokenWithBearer) {
		if (!StringUtils.hasText(accessTokenWithBearer) || !accessTokenWithBearer.startsWith(BEARER_PREFIX)) {
			return null;
		}
		return accessTokenWithBearer.substring(BEARER_PREFIX.length());
	}
}