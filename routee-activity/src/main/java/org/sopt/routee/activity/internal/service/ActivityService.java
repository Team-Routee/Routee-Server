package org.sopt.routee.activity.internal.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.exception.AlreadyInProgressActivityException;
import org.sopt.routee.activity.internal.mapper.ActivityMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.util.TimeZoneUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {

	private static final DateTimeFormatter TITLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	private static final Set<ActivityStatus> ACTIVE_STATUSES = EnumSet.of(
		ActivityStatus.ACTIVITY_IN_PROGRESS,
		ActivityStatus.ACTIVITY_PAUSED
	);

	private final ActivityRepository activityRepository;

	@Transactional
	public CreateActivityResult create(CreateActivityCommand command) {
		if (activityRepository.existsByMemberIdAndActivityStatusIn(
			command.memberId(),
			ACTIVE_STATUSES
		)) {
			throw new AlreadyInProgressActivityException();
		}

		Instant startedAt = command.startedAt()
			.atZone(command.timeZone())
			.toInstant();
		LocalDate activityDate = TimeZoneUtils.toLocalDate(startedAt, command.timeZone());
		String title = activityDate.format(TITLE_DATE_FORMATTER) + " 기록";
		Activity activity = ActivityMapper.toEntity(command, title, startedAt);
		Activity savedActivity = activityRepository.save(activity);
		return new CreateActivityResult(savedActivity.getId(), title);
	}
}
