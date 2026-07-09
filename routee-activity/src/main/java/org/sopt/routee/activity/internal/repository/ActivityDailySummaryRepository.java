package org.sopt.routee.activity.internal.repository;

import java.time.LocalDate;
import java.util.List;

import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityDailySummaryRepository extends JpaRepository<ActivityDailySummary, Long> {

	List<ActivityDailySummary> findByMemberIdAndActivityDateBetweenOrderByActivityDateAsc(
		Long memberId, LocalDate startDate, LocalDate endDate
	);
}
