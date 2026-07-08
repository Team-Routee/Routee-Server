package org.sopt.routee.activity.internal.service;

import java.time.Instant;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.TimelineAlreadyExistsException;
import org.sopt.routee.activity.internal.mapper.TimelineMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateTimelineCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimelineService {

	private final ActivityRepository activityRepository;
	private final TimelineRepository timelineRepository;

	@Transactional
	public void create(CreateTimelineCommand command) {
		Activity activity = activityRepository.findByIdAndMemberId(command.activityId(), command.memberId())
			.orElseThrow(ActivityNotFoundException::new);

		if (timelineRepository.existsByActivityIdAndTrackPointIndex(command.activityId(), command.trackPointIndex())) {
			throw new TimelineAlreadyExistsException();
		}

		Instant recordedAt = command.recordedAt()
			.atZone(command.timeZone())
			.toInstant();

		timelineRepository.save(TimelineMapper.toEntity(command, activity, recordedAt));
	}
}
