package org.sopt.routee.activity.internal.repository;

import java.util.List;

import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimelineRepository extends JpaRepository<Timeline, Long> {
	boolean existsByActivityIdAndTrackPointIndex(Long activityId, Integer trackPointIndex);

	List<Timeline> findByActivityIdOrderByCreatedAtAsc(Long activityId);

	List<Timeline> findByActivityIdAndTimelineStatusOrderByTrackPointIndexAsc(Long activityId, TimelineStatus timelineStatus);

	List<Timeline> findByActivityIdInAndTimelineStatusOrderByCreatedAtAsc(List<Long> activityIds, TimelineStatus timelineStatus);

	@Modifying
	@Query("DELETE FROM Timeline t WHERE t.activity.memberId = :memberId")
	void deleteTimelinesByMemberId(@Param("memberId") Long memberId);
}
