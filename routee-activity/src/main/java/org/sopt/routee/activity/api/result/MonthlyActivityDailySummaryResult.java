package org.sopt.routee.activity.api.result;

import java.time.LocalDate;

public record MonthlyActivityDailySummaryResult(
	LocalDate activityDate,
	Integer totalDurationSeconds,
	Integer activityCount,
	String coverImageUrl
) {
}
