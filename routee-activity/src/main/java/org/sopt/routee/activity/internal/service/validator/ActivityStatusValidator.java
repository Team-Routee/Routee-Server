package org.sopt.routee.activity.internal.service.validator;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.springframework.stereotype.Component;

@Component
public class ActivityStatusValidator {
	public boolean validate(ActivityStatus status) {
		return status == ActivityStatus.ACTIVITY_IN_PROGRESS || status == ActivityStatus.ACTIVITY_PAUSED;
	}
}
