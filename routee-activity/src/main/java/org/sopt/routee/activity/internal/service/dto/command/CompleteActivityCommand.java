package org.sopt.routee.activity.internal.service.dto.command;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.sopt.routee.activity.internal.service.dto.vo.TrackPoint;

public record CompleteActivityCommand(
	Long activityId,
	Long memberId,
	ZoneId timeZone,
	String title,
	Integer distance,
	Integer durationSec,
	Integer maxElevation,
	String mapImageUrl,
	String coverImageObjectKey,
	List<TrackPoint> track,
	LocalDateTime endedAt
) {
}
