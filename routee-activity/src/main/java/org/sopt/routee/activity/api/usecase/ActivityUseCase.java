package org.sopt.routee.activity.api.usecase;

import java.time.YearMonth;
import java.util.List;

import org.sopt.routee.activity.api.result.ActivityDailySummaryResult;

public interface ActivityUseCase {

	List<ActivityDailySummaryResult> getMonthlySummaries(Long memberId, YearMonth yearMonth);

}
