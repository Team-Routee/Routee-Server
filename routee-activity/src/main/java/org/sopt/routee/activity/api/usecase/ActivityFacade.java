package org.sopt.routee.activity.api.usecase;

import java.time.YearMonth;
import java.util.List;

import org.sopt.routee.activity.api.result.MonthlyActivityDailySummaryResult;
import org.sopt.routee.activity.internal.service.ActivityDailySummaryService;
import org.sopt.routee.activity.internal.service.ActivityService;
import org.sopt.routee.activity.internal.service.RouteService;
import org.sopt.routee.activity.internal.service.TimelineService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActivityFacade implements ActivityUseCase {
	private final ActivityService activityService;
	private final RouteService routeService;
	private final ActivityDailySummaryService activityDailySummaryService;
	private final TimelineService timelineService;

	public List<MonthlyActivityDailySummaryResult> getMonthlySummaries(Long memberId, YearMonth yearMonth) {
		return activityDailySummaryService.getMonthlySummaries(memberId, yearMonth).stream()
			.map(MonthlyActivityDailySummaryResult::from)
			.toList();
	}

	@Transactional
	public void deleteForMemberWithdrawal(long memberId) {
		routeService.deleteRoutesByMemberId(memberId);
		activityDailySummaryService.deleteActivityDailySummariesByMemberId(memberId);
		timelineService.deleteTimelinesByMemberId(memberId);
		activityService.deleteActivitiesByMemberId(memberId);
	}
}
