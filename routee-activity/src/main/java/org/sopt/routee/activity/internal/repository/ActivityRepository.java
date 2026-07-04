package org.sopt.routee.activity.internal.repository;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
