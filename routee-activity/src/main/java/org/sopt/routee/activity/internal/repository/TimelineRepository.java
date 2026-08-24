package org.sopt.routee.activity.internal.repository;

import java.util.List;
import java.util.Optional;

import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;
import org.sopt.routee.activity.internal.repository.projection.TimelineImageDeleteTargetProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimelineRepository extends JpaRepository<Timeline, Long> {
	boolean existsByActivityIdAndTrackPointIndex(Long activityId, Integer trackPointIndex);

	Optional<Timeline> findByIdAndActivity_IdAndActivity_MemberId(Long id, Long activityId, Long memberId);

	List<Timeline> findByActivityIdOrderByCreatedAtAsc(Long activityId);

	List<Timeline> findByActivityIdAndTimelineStatusOrderByTrackPointIndexAsc(Long activityId,
		TimelineStatus timelineStatus);

	List<Timeline> findByActivityIdInAndTimelineStatusOrderByCreatedAtAsc(List<Long> activityIds,
		TimelineStatus timelineStatus);

	@Query("""
		SELECT t.activity.id AS activityId, t.timelineImageObjectKey AS objectKey
		FROM Timeline t
		WHERE t.activity.id IN :activityIds
		""")
	List<TimelineImageDeleteTargetProjection> findImageDeleteTargetsByActivityIdIn(
		@Param("activityIds") List<Long> activityIds
	);

	@Modifying
	@Query("DELETE FROM Timeline t WHERE t.activity.memberId = :memberId")
	void deleteTimelinesByMemberId(@Param("memberId") Long memberId);

	@Modifying
	@Query("DELETE FROM Timeline t WHERE t.activity.id IN :activityIds")
	void deleteByActivityIdIn(@Param("activityIds") List<Long> activityIds);
}
