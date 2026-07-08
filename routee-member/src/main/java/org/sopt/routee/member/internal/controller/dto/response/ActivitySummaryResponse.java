package org.sopt.routee.member.internal.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

import org.sopt.routee.member.internal.service.dto.result.ActivitySummaryResult;
import org.sopt.routee.member.internal.service.dto.result.DailySummary;

public record ActivitySummaryResponse(
	List<ActivitySummaryItem> result,
	int year,
	int month
) {
	public static ActivitySummaryResponse from(ActivitySummaryResult result) {
		List<ActivitySummaryItem> items = result.summaries().stream()
			.map(ActivitySummaryItem::from)
			.toList();

		return new ActivitySummaryResponse(items, result.year(), result.month());
	}

	public record ActivitySummaryItem(
		LocalDate activityDate,
		int totalDurationMinutes,
		int activityCount,
		String coverImage
	) {
		public static ActivitySummaryItem from(DailySummary summary) {
			return new ActivitySummaryItem(
				summary.activityDate(),
				summary.totalDurationSeconds() / 60,
				summary.activityCount(),
				summary.coverImageUrl()
			);
		}
	}
}
