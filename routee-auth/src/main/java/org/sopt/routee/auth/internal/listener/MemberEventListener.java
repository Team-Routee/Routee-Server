package org.sopt.routee.auth.internal.listener;

import org.sopt.routee.auth.internal.service.AuthService;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MemberEventListener {

	private final AuthService authService;

	@ApplicationModuleListener
	void handle(MemberWithdrawnEvent event) {
		authService.revokeTokens(event.accessTokenHash(), event.refreshTokenHash());
	}
}
