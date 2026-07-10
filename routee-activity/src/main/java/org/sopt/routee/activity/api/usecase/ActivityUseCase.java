package org.sopt.routee.activity.api.usecase;

import java.time.YearMonth;
import java.util.List;

import org.sopt.routee.activity.api.result.MonthlyActivityDailySummaryResult;

public interface ActivityUseCase {

	List<MonthlyActivityDailySummaryResult> getMonthlySummaries(Long memberId, YearMonth yearMonth);

	void deleteForMemberWithdrawal(long memberId);
}
