package org.sopt.routee.logging.internal;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.sopt.routee.logging.MdcKeys;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@Component
@Order(RequestLoggingFilter.ORDER)
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

	static final int ORDER = -1000;

	private static final String TRACE_ID_HEADER = "X-Trace-Id";

	private final RequestModuleResolver requestModuleResolver;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		Map<String, String> previousContext = MDC.getCopyOfContextMap();

		String traceId = MDC.get(MdcKeys.TRACE_ID);
		if (!StringUtils.hasText(traceId)) {
			traceId = UUID.randomUUID().toString();
		}

		MDC.put(MdcKeys.TRACE_ID, traceId);
		MDC.put(MdcKeys.METHOD, request.getMethod());
		MDC.put(MdcKeys.URI, request.getRequestURI());
		MDC.put(MdcKeys.REQUEST_MODULE, requestModuleResolver.resolve(request.getRequestURI()));

		response.setHeader(TRACE_ID_HEADER, traceId);

		long startTime = System.currentTimeMillis();
		try {
			filterChain.doFilter(request, response);
		} finally {
			long durationMs = System.currentTimeMillis() - startTime;
			MDC.put(MdcKeys.STATUS, String.valueOf(response.getStatus()));
			MDC.put(MdcKeys.DURATION_MS, String.valueOf(durationMs));

			log.info("{} {} - {} ({}ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs);

			if (previousContext != null) {
				MDC.setContextMap(previousContext);
			} else {
				MDC.clear();
			}
		}
	}
}
