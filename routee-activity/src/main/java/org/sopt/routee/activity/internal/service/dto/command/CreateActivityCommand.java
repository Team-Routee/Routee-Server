package org.sopt.routee.activity.internal.service.dto.command;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.activity.ActivityType;

public record CreateActivityCommand(
	Long memberId,
	ActivityType activityType,
	LocalDateTime startedAt,
	ZoneId timeZone
) {
}
