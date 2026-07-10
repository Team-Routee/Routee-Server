package org.sopt.routee.activity.internal.service.dto.result;

import java.time.LocalDate;

public record ActivityStatisticsResult(
	Long activityId,
	String title,
	LocalDate activityDate,
	Integer distanceMeter,
	Integer durationSec,
	Integer maxElevationMeter
) {
}
