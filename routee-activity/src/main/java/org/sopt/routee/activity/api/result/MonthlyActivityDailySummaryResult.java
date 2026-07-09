package org.sopt.routee.activity.api.result;

import java.time.LocalDate;

import org.sopt.routee.activity.internal.service.dto.result.ActivityDailySummaryResult;

public record MonthlyActivityDailySummaryResult(
	LocalDate activityDate,
	Integer totalDurationSeconds,
	Integer activityCount,
	String coverImageUrl
) {
	public static MonthlyActivityDailySummaryResult from(ActivityDailySummaryResult activityDailySummaryResult) {
		return new MonthlyActivityDailySummaryResult(
			activityDailySummaryResult.activityDate(),
			activityDailySummaryResult.totalDurationSeconds(),
			activityDailySummaryResult.activityCount(),
			activityDailySummaryResult.coverImageUrl()
		);
	}
}
