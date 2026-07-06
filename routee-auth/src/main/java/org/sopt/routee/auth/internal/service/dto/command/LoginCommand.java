package org.sopt.routee.auth.internal.service.dto.command;

import org.sopt.routee.external.api.type.OAuthProvider;

public record LoginCommand(
	OAuthProvider provider,
	String idToken
) {
}
