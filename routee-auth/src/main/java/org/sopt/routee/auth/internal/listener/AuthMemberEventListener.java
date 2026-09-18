package org.sopt.routee.auth.internal.listener;

import org.sopt.routee.auth.internal.service.AuthService;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AuthMemberEventListener {

	private final AuthService authService;

	@Async
	@TransactionalEventListener(fallbackExecution = true)
	void handleMemberWithdrawnEvent(MemberWithdrawnEvent event) {
		authService.revokeTokens(event.accessTokenHash(), event.refreshTokenHash());
	}
}
