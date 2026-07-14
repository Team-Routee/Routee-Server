package org.sopt.routee.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MdcKeys {

	TRACE_ID("traceId"),
	MEMBER_ID("memberId"),
	METHOD("method"),
	URI("uri"),
	REQUEST_MODULE("requestModule"),
	STATUS("status"),
	DURATION_MS("durationMs");

	private final String key;
}
