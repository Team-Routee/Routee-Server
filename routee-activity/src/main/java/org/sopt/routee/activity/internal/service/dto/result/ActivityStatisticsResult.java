package org.sopt.routee.activity.internal.service.dto.result;

public record ActivityStatisticsResult(
	Long activityId,
	String title,
	String activityDate,
	Integer distanceMeter,
	Integer durationSec,
	Integer maxElevationMeter
) {
}
