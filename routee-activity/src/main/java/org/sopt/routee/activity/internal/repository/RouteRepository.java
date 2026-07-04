package org.sopt.routee.activity.internal.repository;

import org.sopt.routee.activity.internal.entity.route.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
