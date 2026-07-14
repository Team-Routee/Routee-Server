package org.sopt.routee.logging.internal;

import org.springframework.stereotype.Component;

@Component
public class RequestModuleResolver {

	private static final String API_PREFIX = "/api/v1";

	public String resolve(String uri) {
		if (uri.startsWith(API_PREFIX + "/auth")) {
			return "auth";
		}

		if (uri.startsWith(API_PREFIX + "/member")) {
			return "member";
		}

		if (uri.startsWith(API_PREFIX + "/activity")
			|| uri.startsWith(API_PREFIX + "/archive")) {
			return "activity";
		}

		if (uri.startsWith("/actuator")) {
			return "actuator";
		}

		return "app";
	}
}
