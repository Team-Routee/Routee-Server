package org.sopt.routee.activity.internal.mapper;

import org.sopt.routee.activity.api.result.ActivityDailySummaryResult;
import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityDailySummaryMapper {

	public static ActivityDailySummaryResult toResult(ActivityDailySummary summary) {
		return new ActivityDailySummaryResult(
			summary.getActivityDate(),
			summary.getTotalDurationSec(),
			summary.getActivityCount(),
			summary.getCoverImageUrl()
		);
	}
}
