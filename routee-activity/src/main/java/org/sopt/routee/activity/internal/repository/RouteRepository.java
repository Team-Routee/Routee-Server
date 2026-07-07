package org.sopt.routee.activity.internal.repository;

import java.util.List;

import org.sopt.routee.activity.internal.entity.route.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Long> {

	List<Route> findByActivityIdOrderBySequenceAsc(Long activityId);

	@Modifying
	@Query("DELETE FROM Route r WHERE r.activity.id = :activityId")
	void deleteByActivityId(@Param("activityId") Long activityId);
}
