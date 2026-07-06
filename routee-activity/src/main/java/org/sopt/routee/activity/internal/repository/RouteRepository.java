package org.sopt.routee.activity.internal.repository;

import java.util.List;

import org.sopt.routee.activity.internal.entity.route.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

	List<Route> findByActivityIdOrderBySequenceAsc(Long activityId);

	void deleteByActivityId(Long activityId);
}
