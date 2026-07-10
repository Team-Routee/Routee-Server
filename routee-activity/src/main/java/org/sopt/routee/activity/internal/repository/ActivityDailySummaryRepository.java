package org.sopt.routee.activity.internal.repository;

import java.time.LocalDate;
import java.util.List;

import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ActivityDailySummaryRepository extends JpaRepository<ActivityDailySummary, Long> {

	List<ActivityDailySummary> findByMemberIdAndActivityDateBetweenOrderByActivityDateAsc(
		Long memberId, LocalDate startDate, LocalDate endDate
	);

	@Modifying
	@Query("DELETE FROM ActivityDailySummary ads WHERE ads.memberId = :memberId")
	void deleteByMemberId(Long memberId);
}
