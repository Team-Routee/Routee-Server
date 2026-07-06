package org.sopt.routee.auth.security.handler;

import java.io.IOException;

import org.sopt.routee.auth.internal.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final HandlerExceptionResolver handlerExceptionResolver;

	public JwtAuthenticationEntryPoint(
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
	) {
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException, ServletException {
		handlerExceptionResolver.resolveException(
			request,
			response,
			null,
			new UnauthorizedException()
		);
	}
}
