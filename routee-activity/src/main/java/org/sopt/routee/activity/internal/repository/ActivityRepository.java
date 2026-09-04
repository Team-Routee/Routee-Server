package org.sopt.routee.activity.internal.repository;

import java.time.Instant;
import java.time.LocalDate;
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

	@Query("SELECT a.id FROM Activity a WHERE a.memberId = :memberId AND a.activityStatus IN :activityStatuses")
	List<Long> findIdsByMemberIdAndActivityStatusIn(
		@Param("memberId") Long memberId,
		@Param("activityStatuses") Collection<ActivityStatus> activityStatuses
	);

	boolean existsByIdAndMemberId(Long id, Long memberId);

	Optional<Activity> findByIdAndMemberId(Long id, Long memberId);

	List<Activity> findByMemberIdAndActivityStatusAndStartedAtBetweenOrderByStartedAtDesc(
		Long memberId,
		ActivityStatus activityStatus,
		Instant startedAtFrom,
		Instant startedAtTo
	);

	Optional<Activity> findFirstByMemberIdAndActivityDateWithTimezoneAndActivityStatusAndCoverImageObjectKeyIsNotNullOrderByStartedAtAsc(
		Long memberId,
		LocalDate activityDateWithTimezone,
		ActivityStatus activityStatus
	);

	@Modifying
	@Query("DELETE FROM Activity a WHERE a.memberId = :memberId")
	void deleteByMemberId(@Param("memberId") Long memberId);

	@Modifying
	@Query("DELETE FROM Activity a WHERE a.id IN :activityIds")
	void deleteByIdIn(@Param("activityIds") List<Long> activityIds);

	@Query(value = "SELECT pg_advisory_xact_lock(:memberId)", nativeQuery = true)
	void acquireCreationLock(@Param("memberId") Long memberId);
}
