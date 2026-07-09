package org.sopt.routee.activity.internal.repository;

import java.util.List;

import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineRepository extends JpaRepository<Timeline, Long> {
	boolean existsByActivityIdAndTrackPointIndex(Long activityId, Integer trackPointIndex);

	List<Timeline> findByActivityIdOrderByCreatedAtAsc(Long activityId);
}
