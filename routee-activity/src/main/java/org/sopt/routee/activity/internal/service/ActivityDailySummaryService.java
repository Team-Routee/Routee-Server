package org.sopt.routee.activity.internal.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;
import org.sopt.routee.activity.internal.service.dto.result.ActivityDailySummaryResult;
import org.sopt.routee.activity.internal.mapper.ActivityDailySummaryMapper;
import org.sopt.routee.activity.internal.repository.ActivityDailySummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityDailySummaryService {

	private final ActivityDailySummaryRepository activityDailySummaryRepository;

	@Transactional(readOnly = true)
	public List<ActivityDailySummaryResult> getMonthlySummaries(Long memberId, YearMonth yearMonth) {
		return activityDailySummaryRepository.findByMemberIdAndActivityDateBetweenOrderByActivityDateAsc(
				memberId, yearMonth.atDay(1), yearMonth.atEndOfMonth()
			)
			.stream()
			.map(ActivityDailySummaryMapper::toResult)
			.toList();
	}

	@Transactional
	public void recordActivity(Long memberId, LocalDate activityDate, Integer durationSec) {
		if (activityDailySummaryRepository.existsByMemberIdAndActivityDate(memberId, activityDate)) {
			activityDailySummaryRepository.incrementDailySummary(memberId, activityDate, durationSec);
			return;
		}

		activityDailySummaryRepository.save(
			ActivityDailySummary.builder()
				.memberId(memberId)
				.activityDate(activityDate)
				.totalDurationSec(durationSec)
				.activityCount(1)
				.build()
		);
	}

	@Transactional
	public void deleteActivityDailySummariesByMemberId(long memberId) {
		activityDailySummaryRepository.deleteByMemberId(memberId);
	}
}
