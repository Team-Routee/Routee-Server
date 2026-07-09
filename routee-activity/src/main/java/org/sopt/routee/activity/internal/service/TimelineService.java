package org.sopt.routee.activity.internal.service;

import java.time.Instant;
import java.util.List;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.mapper.TimelineMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateTimelineCommand;
import org.sopt.routee.activity.internal.service.dto.result.TimelineResult;
import org.sopt.routee.util.TimeZoneUtils;
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

		Instant recordedAt = TimeZoneUtils.toUtcInstantTime(command.recordedAt(), command.timeZone());

		timelineRepository.save(TimelineMapper.toEntity(command, activity, recordedAt));
	}

	@Transactional(readOnly = true)
	public List<TimelineResult> getTimelines(Long activityId, Long memberId) {
		if (!activityRepository.existsByIdAndMemberId(activityId, memberId)) {
			throw new ActivityNotFoundException();
		}

		return timelineRepository.findByActivityIdOrderByCreatedAtAsc(activityId).stream()
			.map(TimelineMapper::toTimelineResult)
			.toList();
	}
}
