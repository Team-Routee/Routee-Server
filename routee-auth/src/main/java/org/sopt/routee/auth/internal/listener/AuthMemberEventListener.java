package org.sopt.routee.auth.internal.listener;

import org.sopt.routee.auth.internal.service.AuthService;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AuthMemberEventListener {

	private final AuthService authService;

	@ApplicationModuleListener
	void handleMemberWithdrawnEvent(MemberWithdrawnEvent event) {
		authService.revokeTokens(event.accessTokenHash(), event.refreshTokenHash());
	}
}
