package org.sopt.routee.auth.security;

import java.io.IOException;
import java.util.List;

import org.sopt.routee.auth.internal.exception.InvalidTokenException;
import org.sopt.routee.auth.internal.jwt.JwtParser;
import org.sopt.routee.auth.internal.jwt.JwtValidator;
import org.sopt.routee.auth.internal.repository.TokenBlacklistRepository;
import org.sopt.routee.auth.security.util.AuthWhiteList;
import org.sopt.routee.exception.BaseException;
import org.sopt.routee.logging.MdcKeys;
import org.sopt.routee.member.api.type.MemberRole;
import org.sopt.routee.member.api.usecase.MemberUseCase;
import org.sopt.routee.util.TokenExtractor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtValidator jwtValidator;
	private final JwtParser jwtParser;
	private final TokenBlacklistRepository tokenBlacklistRepository;
	private final MemberUseCase memberUseCase;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public JwtAuthenticationFilter(
		JwtValidator jwtValidator,
		JwtParser jwtParser,
		TokenBlacklistRepository tokenBlacklistRepository,
		MemberUseCase memberUseCase,
		@Qualifier("handlerExceptionResolver")
		HandlerExceptionResolver handlerExceptionResolver
	) {
		this.jwtValidator = jwtValidator;
		this.jwtParser = jwtParser;
		this.tokenBlacklistRepository = tokenBlacklistRepository;
		this.memberUseCase = memberUseCase;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return AuthWhiteList.isPermitted(request);
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		try {
			String accessToken = TokenExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));

			if (accessToken != null) {
				if (tokenBlacklistRepository.isBlacklisted(accessToken)) {
					throw new InvalidTokenException();
				}

				Claims claims = jwtValidator.validate(accessToken);

				long memberId = jwtParser.extractMemberId(claims);
				MemberRole role = jwtParser.extractMemberRole(claims);

				if (!memberUseCase.existsById(memberId)) {
					throw new InvalidTokenException();
				}

				MDC.put(MdcKeys.MEMBER_ID.getKey(), String.valueOf(memberId));

				SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(
						memberId,
						null,
						List.of(new SimpleGrantedAuthority(role.name()))
					)
				);
			}
		} catch (BaseException e) {
			SecurityContextHolder.clearContext();
			handlerExceptionResolver.resolveException(request, response, null, e);
			return;
		}

		filterChain.doFilter(request, response);
	}
}
