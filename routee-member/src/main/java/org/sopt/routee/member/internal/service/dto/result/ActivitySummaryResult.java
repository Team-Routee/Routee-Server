package org.sopt.routee.member.internal.service.dto.result;

import java.util.List;

public record ActivitySummaryResult(
	List<DailySummary> summaries,
	int year,
	int month
) {
}
