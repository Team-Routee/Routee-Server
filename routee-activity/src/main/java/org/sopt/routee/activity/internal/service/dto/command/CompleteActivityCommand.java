package org.sopt.routee.activity.internal.service.dto.command;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.activity.ActivityType;

public record CompleteActivityCommand(
	Long activityId,
	Long memberId,
	ZoneId timeZone,
	String title,
	ActivityType activityType,
	ActivityStatus status,
	Integer distance,
	Integer durationSec,
	Integer maxElevation,
	String mapImageUrl,
	String coverImageObjectKey,
	String track,
	LocalDateTime startedAt,
	LocalDateTime endedAt
) {
}
