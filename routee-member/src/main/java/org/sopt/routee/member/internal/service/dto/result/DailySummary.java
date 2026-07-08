package org.sopt.routee.member.internal.service.dto.result;

import java.time.LocalDate;

public record DailySummary(
	LocalDate activityDate,
	int totalDurationSeconds,
	int activityCount,
	String coverImageUrl
) {
}
