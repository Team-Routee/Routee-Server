package org.sopt.routee.activity.internal.repository;

import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityDailySummaryRepository extends JpaRepository<ActivityDailySummary, Long> {
}
