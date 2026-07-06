package org.sopt.routee.auth.security.util;

import java.util.Arrays;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public enum AuthWhiteList {

	AUTH_HEALTH(HttpMethod.GET, "/api/v1/auth/health"),
	SOCIAL_LOGIN(HttpMethod.POST, "/api/v1/auth/login"),
	MEMBER_REGISTER(HttpMethod.POST, "/api/v1/member/register"),
	TOKEN_REISSUE(HttpMethod.POST, "/api/v1/auth/reissue"),

	SWAGGER_UI(HttpMethod.GET, "/swagger-ui/**"),
	SWAGGER_API_DOCS(HttpMethod.GET, "/v3/api-docs/**");

	private final HttpMethod method;
	private final String pattern;
	private final RequestMatcher matcher;

	AuthWhiteList(HttpMethod method, String pattern) {
		this.method = method;
		this.pattern = pattern;
		this.matcher = PathPatternRequestMatcher.pathPattern(method, pattern);
	}

	public static RequestMatcher[] requestMatchers() {
		return Arrays.stream(values())
			.map(e -> e.matcher)
			.toArray(RequestMatcher[]::new);
	}

	public static boolean isPermitted(HttpServletRequest request) {
		return Arrays.stream(values())
			.anyMatch(e -> e.matcher.matches(request));
	}
}
