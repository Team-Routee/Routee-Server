package org.sopt.routee.auth.internal.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum JwtClaimKeys {

	TOKEN_TYPE("type"),
	MEMBER_ROLE("role");

	private final String key;
}
