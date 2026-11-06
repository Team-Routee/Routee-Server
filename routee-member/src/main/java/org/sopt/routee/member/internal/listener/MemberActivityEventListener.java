package org.sopt.routee.member.internal.listener;

import org.sopt.routee.activity.api.event.ActivityCompletedEvent;
import org.sopt.routee.activity.api.event.ActivityDeletedEvent;
import org.sopt.routee.member.internal.service.MemberService;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MemberActivityEventListener {

	private final MemberService memberService;

	@ApplicationModuleListener
	void handleActivityCompleted(ActivityCompletedEvent event) {
		memberService.incrementTotalActivityCount(event.memberId());
	}

	@ApplicationModuleListener
	void handleActivityDeleted(ActivityDeletedEvent event) {
		memberService.decrementTotalActivityCount(event.memberId());
	}
}