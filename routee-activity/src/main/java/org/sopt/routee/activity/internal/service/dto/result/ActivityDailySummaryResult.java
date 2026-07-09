package org.sopt.routee.activity.internal.service.dto.result;

import java.time.LocalDate;

public record ActivityDailySummaryResult(
	LocalDate activityDate,
	Integer totalDurationSeconds,
	Integer activityCount,
	String coverImageUrl
) {
}
