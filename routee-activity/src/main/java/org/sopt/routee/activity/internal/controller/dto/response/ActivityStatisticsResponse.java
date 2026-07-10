package org.sopt.routee.activity.internal.controller.dto.response;

import java.time.LocalDate;

import org.sopt.routee.activity.internal.service.dto.result.ActivityStatisticsResult;

public record ActivityStatisticsResponse(
	Long activityId,
	String title,
	LocalDate activityDate,
	Integer distanceMeter,
	Integer durationSec,
	Integer maxElevationMeter
) {
	public static ActivityStatisticsResponse from(ActivityStatisticsResult result) {
		return new ActivityStatisticsResponse(
			result.activityId(),
			result.title(),
			result.activityDate(),
			result.distanceMeter(),
			result.durationSec(),
			result.maxElevationMeter()
		);
	}
}
