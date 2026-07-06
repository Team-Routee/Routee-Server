package org.sopt.routee.member.internal.service.dto.command;

import org.sopt.routee.external.api.type.OAuthProvider;

public record RegisterCommand(
	OAuthProvider provider,
	String idToken,
	String nickname
) {
}
