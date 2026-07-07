package org.sopt.routee.activity.internal.service;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.exception.AlreadyInProgressActivityException;
import org.sopt.routee.activity.internal.mapper.ActivityMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.util.ActivityTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {

	private static final Set<ActivityStatus> ACTIVE_STATUSES = EnumSet.of(
		ActivityStatus.ACTIVITY_IN_PROGRESS,
		ActivityStatus.ACTIVITY_PAUSED
	);

	private final ActivityRepository activityRepository;

	@Transactional
	public Long create(CreateActivityCommand command) {
		if (activityRepository.existsByMemberIdAndActivityStatusIn(
			command.memberId(),
			ACTIVE_STATUSES
		)) {
			throw new AlreadyInProgressActivityException();
		}

		Instant startedAt = ActivityTimeUtils.nowUtc();
		String title = ActivityTimeUtils.formatSeoulDateTitle(startedAt);
		Activity activity = ActivityMapper.toEntity(command, title, startedAt);
		return activityRepository.save(activity).getId();
	}
}
