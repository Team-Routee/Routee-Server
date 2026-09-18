package org.sopt.routee.member.internal.service.dto.command;

import java.time.ZoneId;

import org.sopt.routee.external.api.type.OAuthProvider;

public record RegisterCommand(
	OAuthProvider provider,
	String idToken,
	String nickname,
	AgreementCommand agreement,
	ZoneId timeZone
) {
}
