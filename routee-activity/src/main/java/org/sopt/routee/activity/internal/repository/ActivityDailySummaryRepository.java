package org.sopt.routee.activity.internal.repository;

import java.time.LocalDate;
import java.util.List;

import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityDailySummaryRepository extends JpaRepository<ActivityDailySummary, Long> {

	List<ActivityDailySummary> findByMemberIdAndActivityDateBetweenOrderByActivityDateAsc(
		Long memberId, LocalDate startDate, LocalDate endDate
	);

	boolean existsByMemberIdAndActivityDate(Long memberId, LocalDate activityDate);

	@Modifying
	@Query("""
		UPDATE ActivityDailySummary ads
		SET ads.totalDurationSec = ads.totalDurationSec + :durationSec,
			ads.activityCount = ads.activityCount + 1
		WHERE ads.memberId = :memberId AND ads.activityDate = :activityDate
		""")
	void incrementDailySummary(
		@Param("memberId") Long memberId,
		@Param("activityDate") LocalDate activityDate,
		@Param("durationSec") Integer durationSec
	);

	@Modifying
	@Query("DELETE FROM ActivityDailySummary ads WHERE ads.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") Long memberId);
}
