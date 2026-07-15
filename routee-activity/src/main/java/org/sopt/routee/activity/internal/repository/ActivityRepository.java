package org.sopt.routee.activity.internal.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
	boolean existsByMemberIdAndActivityStatusIn(Long memberId, Collection<ActivityStatus> activityStatuses);

	boolean existsByIdAndMemberId(Long id, Long memberId);

	Optional<Activity> findByIdAndMemberId(Long id, Long memberId);

	List<Activity> findByMemberIdAndActivityStatusAndStartedAtBetweenOrderByStartedAtAsc(
		Long memberId,
		ActivityStatus activityStatus,
		Instant startedAtFrom,
		Instant startedAtTo
	);

	List<Activity> findByMemberIdAndActivityStatusAndStartedAtBetweenOrderByStartedAtDesc(
		Long memberId,
		ActivityStatus activityStatus,
		Instant startedAtFrom,
		Instant startedAtTo
	);

	@Modifying
	@Query("DELETE FROM Activity a WHERE a.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") Long memberId);
}
