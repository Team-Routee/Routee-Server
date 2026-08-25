package org.sopt.routee.activity.internal.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.TimelineNotFoundException;
import org.sopt.routee.activity.internal.mapper.TimelineMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateTimelineCommand;
import org.sopt.routee.activity.internal.service.dto.command.UpdateTimelineTitleCommand;
import org.sopt.routee.activity.internal.service.dto.result.CreateTimelineResult;
import org.sopt.routee.activity.internal.service.dto.result.TimelineResult;
import org.sopt.routee.activity.internal.service.dto.result.UpdateTimelineTitleResult;
import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.command.FileImageAccessUrlCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;
import org.sopt.routee.util.TimeZoneUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineService {

	private final ActivityRepository activityRepository;
	private final TimelineRepository timelineRepository;
	private final ActivityDailySummaryService activityDailySummaryService;
	private final FileImageAccessUrlPort fileImageAccessUrlPort;
	private final FileDeletePort fileDeletePort;
	private final TransactionTemplate transactionTemplate;

	@Transactional
	public CreateTimelineResult create(CreateTimelineCommand command) {
		Activity activity = activityRepository.findByIdAndMemberId(command.activityId(), command.memberId())
			.orElseThrow(ActivityNotFoundException::new);

		Instant createdAt = TimeZoneUtils.toUtcInstantTime(command.createdAt(), command.timeZone());

		Timeline timeline = timelineRepository.save(TimelineMapper.toEntity(command, activity, createdAt));

		return new CreateTimelineResult(timeline.getId());
	}

	@Transactional(readOnly = true)
	public List<TimelineResult> getTimelines(Long activityId, Long memberId) {
		if (!activityRepository.existsByIdAndMemberId(activityId, memberId)) {
			throw new ActivityNotFoundException();
		}

		return timelineRepository.findByActivityIdOrderByCreatedAtAsc(activityId).stream()
			.map(timeline -> TimelineMapper.toTimelineResult(timeline, generateImageUrl(memberId, activityId, timeline)))
			.toList();
	}

	public void delete(Long activityId, Long timelineId, Long memberId) {
		Timeline timeline = transactionTemplate.execute(status -> {
			Timeline ownedTimeline = findOwnedTimeline(activityId, timelineId, memberId);

			timelineRepository.delete(ownedTimeline);
			refreshCoverImageIfDeleted(ownedTimeline);

			return ownedTimeline;
		});

		String objectKey = timeline.getTimelineImageObjectKey();

		Thread.startVirtualThread(() -> deleteTimelineImage(memberId, activityId, objectKey));
	}

	@Transactional
	public UpdateTimelineTitleResult updateTitle(UpdateTimelineTitleCommand command) {
		Timeline timeline = findOwnedTimeline(command.activityId(), command.timelineId(), command.memberId());

		timeline.updateTitle(command.title());

		return TimelineMapper.toTitleUpdateResult(timeline);
	}

	@Transactional
	public void deleteTimelinesByMemberId(long memberId) {
		timelineRepository.deleteTimelinesByMemberId(memberId);
	}

	private String generateImageUrl(Long memberId, Long activityId, Timeline timeline) {
		FileImageAccessUrlCommand command = new FileImageAccessUrlCommand(
			FileUploadDirectory.TIMELINE,
			FileUploadImageSize.LARGE,
			memberId.toString(),
			activityId.toString(),
			timeline.getTimelineImageObjectKey()
		);

		return fileImageAccessUrlPort.generateImageUrl(command).imageUrl();
	}

	private void refreshCoverImageIfDeleted(Timeline deletedTimeline) {
		Activity activity = deletedTimeline.getActivity();
		if (!deletedTimeline.getTimelineImageObjectKey().equals(activity.getCoverImageObjectKey())) {
			return;
		}

		String newCoverImageObjectKey = timelineRepository
			.findFirstByActivityIdAndTimelineStatusOrderByTrackPointIndexAsc(
				activity.getId(), TimelineStatus.SUCCESSFUL_CREATED)
			.map(Timeline::getTimelineImageObjectKey)
			.orElse(null);

		activity.updateCoverImageObjectKey(newCoverImageObjectKey);
		refreshDailySummaryCoverIfNeeded(activity);
	}

	private void refreshDailySummaryCoverIfNeeded(Activity activity) {
		LocalDate activityDate = activity.getActivityDateWithTimezone();
		if (activityDate == null) {
			return;
		}

		Activity firstActivityOfDay = activityRepository
			.findFirstByMemberIdAndActivityDateWithTimezoneAndActivityStatusOrderByStartedAtAsc(
				activity.getMemberId(), activityDate, ActivityStatus.ACTIVITY_COMPLETED)
			.orElse(null);

		Long coverActivityId = firstActivityOfDay == null ? null : firstActivityOfDay.getId();
		String coverImageObjectKey = firstActivityOfDay == null ? null : firstActivityOfDay.getCoverImageObjectKey();

		activityDailySummaryService.refreshCoverImage(activity.getMemberId(), activityDate, coverActivityId, coverImageObjectKey);
	}

	private Timeline findOwnedTimeline(Long activityId, Long timelineId, Long memberId) {
		return timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId)
			.orElseThrow(() -> activityRepository.existsByIdAndMemberId(activityId, memberId)
				? new TimelineNotFoundException()
				: new ActivityNotFoundException());
	}

	private void deleteTimelineImage(Long memberId, Long activityId, String objectKey) {
		try {
			fileDeletePort.deleteImage(
				new FileDeleteCommand(FileUploadDirectory.TIMELINE, memberId.toString(), activityId.toString(), objectKey));
		} catch (BaseException e) {
			log.warn("Timeline image delete failed. activityId={}, objectKey={}", activityId, objectKey, e);
		}
	}
}
