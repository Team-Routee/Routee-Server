package org.sopt.routee.activity.internal.mapper;

import java.time.Instant;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;

public class ActivityMapper {

	private ActivityMapper() {
	}

	public static Activity toEntity(CreateActivityCommand command, String title, Instant startedAt) {
		return Activity.builder()
			.title(title)
			.activityType(command.activityType())
			.activityStatus(ActivityStatus.ACTIVITY_IN_PROGRESS)
			.startedAt(startedAt)
			.memberId(command.memberId())
			.build();
	}
}
