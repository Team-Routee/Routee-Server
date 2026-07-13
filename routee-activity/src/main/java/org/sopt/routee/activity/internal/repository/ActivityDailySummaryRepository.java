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

	@Modifying
	@Query(value = """
		INSERT INTO activity_daily_summary (id, member_id, activity_date, total_duration_sec, activity_count)
		VALUES (:id, :memberId, :activityDate, :durationSec, 1)
		ON CONFLICT (member_id, activity_date)
		DO UPDATE SET
			total_duration_sec = activity_daily_summary.total_duration_sec + EXCLUDED.total_duration_sec,
			activity_count = activity_daily_summary.activity_count + 1
		""", nativeQuery = true)
	void upsertDailySummary(
		@Param("id") Long id,
		@Param("memberId") Long memberId,
		@Param("activityDate") LocalDate activityDate,
		@Param("durationSec") Integer durationSec
	);

	@Modifying
	@Query("DELETE FROM ActivityDailySummary ads WHERE ads.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") Long memberId);
}
