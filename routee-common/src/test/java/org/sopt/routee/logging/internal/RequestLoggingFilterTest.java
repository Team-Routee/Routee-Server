package org.sopt.routee.logging.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestLoggingFilterTest {

	private final RequestLoggingFilter filter = new RequestLoggingFilter(new RequestModuleResolver());

	@AfterEach
	void tearDown() {
		MDC.clear();
	}

	@Test
	void 요청_처리_중에는_MDC에_traceId_method_uri가_채워진다() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/routes");
		MockHttpServletResponse response = new MockHttpServletResponse();
		Map<String, String>[] mdcDuringRequest = new Map[1];
		MockFilterChain chain = new MockFilterChain() {
			@Override
			public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
				mdcDuringRequest[0] = MDC.getCopyOfContextMap();
			}
		};

		filter.doFilter(request, response, chain);

		assertThat(mdcDuringRequest[0])
			.containsEntry("method", "GET")
			.containsEntry("uri", "/api/v1/routes")
			.containsEntry("requestModule", "app");
		assertThat(mdcDuringRequest[0].get("traceId")).isNotBlank();
	}

	@Test
	void 요청이_끝나면_MDC가_clear된다() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/routes");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(200);
		MockFilterChain chain = new MockFilterChain();

		filter.doFilter(request, response, chain);

		assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
	}

	@Test
	void 기존_MDC에_traceId가_있으면_그대로_재사용한다() throws Exception {
		MDC.put("traceId", "existing-otel-trace-id");
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/routes");
		MockHttpServletResponse response = new MockHttpServletResponse();
		Map<String, String>[] mdcDuringRequest = new Map[1];
		MockFilterChain chain = new MockFilterChain() {
			@Override
			public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
				mdcDuringRequest[0] = MDC.getCopyOfContextMap();
			}
		};

		filter.doFilter(request, response, chain);

		assertThat(mdcDuringRequest[0]).containsEntry("traceId", "existing-otel-trace-id");
	}
}
