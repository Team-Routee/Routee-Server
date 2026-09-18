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
		INSERT INTO activity_daily_summary (
			id, member_id, activity_date, total_duration_sec, cover_activity_id, cover_image_object_key, activity_count
		)
		VALUES (:id, :memberId, :activityDate, :durationSec, :coverActivityId, :coverImageObjectKey, 1)
		ON CONFLICT (member_id, activity_date)
		DO UPDATE SET
			total_duration_sec = activity_daily_summary.total_duration_sec + EXCLUDED.total_duration_sec,
			cover_activity_id = CASE WHEN activity_daily_summary.cover_image_object_key IS NULL
				THEN EXCLUDED.cover_activity_id ELSE activity_daily_summary.cover_activity_id END,
			cover_image_object_key = CASE WHEN activity_daily_summary.cover_image_object_key IS NULL
				THEN EXCLUDED.cover_image_object_key ELSE activity_daily_summary.cover_image_object_key END,
			activity_count = activity_daily_summary.activity_count + 1
		""", nativeQuery = true)
	void upsertDailySummary(
		@Param("id") Long id,
		@Param("memberId") Long memberId,
		@Param("activityDate") LocalDate activityDate,
		@Param("durationSec") Integer durationSec,
		@Param("coverActivityId") Long coverActivityId,
		@Param("coverImageObjectKey") String coverImageObjectKey
	);

	@Modifying
	@Query("DELETE FROM ActivityDailySummary ads WHERE ads.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") Long memberId);

	@Modifying
	@Query("""
		UPDATE ActivityDailySummary ads SET ads.coverActivityId = :coverActivityId, ads.coverImageObjectKey = :coverImageObjectKey
		WHERE ads.memberId = :memberId AND ads.activityDate = :activityDate AND ads.coverActivityId = :previousCoverActivityId
		""")
	void updateCoverImage(
		@Param("memberId") Long memberId,
		@Param("activityDate") LocalDate activityDate,
		@Param("coverActivityId") Long coverActivityId,
		@Param("coverImageObjectKey") String coverImageObjectKey,
		@Param("previousCoverActivityId") Long previousCoverActivityId
	);

	@Modifying
	@Query("""
		UPDATE ActivityDailySummary ads
		SET ads.totalDurationSec = ads.totalDurationSec - :durationSec, ads.activityCount = ads.activityCount - 1
		WHERE ads.memberId = :memberId AND ads.activityDate = :activityDate
		""")
	void decrementDailySummary(
		@Param("memberId") Long memberId,
		@Param("activityDate") LocalDate activityDate,
		@Param("durationSec") Integer durationSec
	);

	@Modifying
	@Query("DELETE FROM ActivityDailySummary ads WHERE ads.memberId = :memberId AND ads.activityDate = :activityDate AND ads.activityCount <= 0")
	void deleteIfEmpty(@Param("memberId") Long memberId, @Param("activityDate") LocalDate activityDate);
}
