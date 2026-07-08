package org.sopt.routee.activity.internal.service.dto.command;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;

public record CreateTimelineCommand(
	Long memberId,
	Long activityId,
	String title,
	String objectKey,
	LocalDateTime recordedAt,
	Integer trackPointIndex,
	Double longitude,
	Double latitude,
	Double altitude,
	Integer measure,
	TimelineStatus status,
	ZoneId timeZone
) {
}
