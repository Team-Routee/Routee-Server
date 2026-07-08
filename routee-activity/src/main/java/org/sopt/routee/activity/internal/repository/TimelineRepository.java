package org.sopt.routee.activity.internal.repository;

import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineRepository extends JpaRepository<Timeline, Long> {
	boolean existsByActivityIdAndTrackPointIndex(Long activityId, Integer trackPointIndex);
}
