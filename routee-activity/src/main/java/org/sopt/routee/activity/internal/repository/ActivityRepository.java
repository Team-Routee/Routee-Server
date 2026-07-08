package org.sopt.routee.activity.internal.repository;

import java.util.Collection;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
	boolean existsByMemberIdAndActivityStatusIn(Long memberId, Collection<ActivityStatus> activityStatuses);

	boolean existsByIdAndMemberId(Long id, Long memberId);
}
