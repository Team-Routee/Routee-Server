package org.sopt.routee.activity.internal.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.sopt.routee.activity.api.event.ActivityCompletedEvent;
import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;
import org.sopt.routee.activity.internal.exception.ActivityAlreadyCompletedException;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.ActivityStatusAlreadySameException;
import org.sopt.routee.activity.internal.exception.AlreadyInProgressActivityException;
import org.sopt.routee.activity.internal.exception.InvalidActivityStatusTransitionException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.activity.internal.mapper.ActivityMapper;
import org.sopt.routee.activity.internal.mapper.ActivityTrackMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.vo.TrackPoint;
import org.sopt.routee.activity.internal.repository.RouteRepository;
import org.sopt.routee.activity.internal.service.dto.command.CompleteActivityCommand;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.command.GetActivityRecapCommand;
import org.sopt.routee.activity.internal.service.dto.command.ImageUploadUrlCommand;
import org.sopt.routee.activity.internal.service.dto.command.UpdateActivityStatusCommand;
import org.sopt.routee.activity.internal.service.dto.result.ActivityEditItemResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityEditListResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityRecapResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityStatisticsResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityTrackResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivitiesByDateResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityPreviewResult;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.activity.internal.service.dto.result.TimelineMarkerResult;
import org.sopt.routee.activity.internal.service.dto.result.TrackPointResult;
import org.sopt.routee.activity.internal.service.dto.result.UpdateActivityStatusResult;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;
import org.sopt.routee.external.api.command.FileImageAccessUrlCommand;
import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileUploadPresignResult;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;
import org.sopt.routee.util.TimeZoneUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

	private static final DateTimeFormatter TITLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	private static final Set<ActivityStatus> ACTIVE_STATUSES = EnumSet.of(
		ActivityStatus.ACTIVITY_IN_PROGRESS,
		ActivityStatus.ACTIVITY_PAUSED
	);
	private static final int MAX_EDIT_LIST_TIMELINE_PHOTO_COUNT = 4;

	private final ActivityRepository activityRepository;
	private final TimelineRepository timelineRepository;
	private final ActivityDailySummaryService activityDailySummaryService;
	private final ActivityImageFileNameValidator activityImageFileNameValidator;
	private final FileUploadPresignPort fileUploadPresignPort;
	private final FileImageAccessUrlPort fileImageAccessUrlPort;
	private final RouteRepository routeRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

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

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				log.info("Activity created. activityId={}, memberId={}", savedActivity.getId(), command.memberId());
			}
		});

		return new CreateActivityResult(savedActivity.getId(), title);
	}

	@Transactional(readOnly = true)
	public ImageUrlResult generateImageUploadUrl(ImageUploadUrlCommand command) {
		if (!activityRepository.existsByIdAndMemberId(command.activityId(), command.memberId())) {
			throw new ActivityNotFoundException();
		}

		if (!activityImageFileNameValidator.validate(command.fileName())) {
			throw new UnsupportedImageFileExtensionException();
		}

		FileUploadPresignCommand presignCommand = new FileUploadPresignCommand(
			command.directory(),
			command.imageSize(),
			command.activityId().toString(),
			command.fileName()
		);
		FileUploadPresignResult result = fileUploadPresignPort.generatePutPresignedUrl(presignCommand);

		return new ImageUrlResult(result.presignedUrl(), result.objectKey());
	}

	@Transactional
	public UpdateActivityStatusResult updateStatus(UpdateActivityStatusCommand command) {
		if (!command.status().isChangeableRequestStatus()) {
			throw new InvalidActivityStatusTransitionException();
		}

		Activity activity = activityRepository.findByIdAndMemberId(command.activityId(), command.memberId())
			.orElseThrow(ActivityNotFoundException::new);

		if (activity.getActivityStatus().isCompleted()) {
			throw new ActivityAlreadyCompletedException();
		}

		if (activity.getActivityStatus().isSameAs(command.status())) {
			throw new ActivityStatusAlreadySameException();
		}

		activity.updateStatus(command.status());
		return ActivityMapper.toStatusUpdateResult(activity);
	}

	@Transactional
	public void complete(CompleteActivityCommand command) {
		Activity activity = activityRepository.findByIdAndMemberId(command.activityId(), command.memberId())
			.orElseThrow(ActivityNotFoundException::new);

		Instant endedAt = TimeZoneUtils.toUtcInstantTime(command.endedAt(), command.timeZone());

		activity.updateCompletedData(
			command.title(),
			command.distance(),
			command.durationSec(),
			command.maxElevation(),
			command.mapImageUrl(),
			command.coverImageObjectKey(),
			ActivityMapper.toLineString(command.track()),
			endedAt
		);

		LocalDate activityDate = TimeZoneUtils.toLocalDate(activity.getStartedAt(), command.timeZone());
		activityDailySummaryService.recordActivity(command.memberId(), activityDate, command.durationSec());

		applicationEventPublisher.publishEvent(new ActivityCompletedEvent(command.memberId()));

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				log.info("Activity completed. activityId={}, memberId={}", command.activityId(), command.memberId());
			}
		});
	}

	@Transactional(readOnly = true)
	public ActivityStatisticsResult getStatistics(Long activityId, Long memberId, ZoneId timeZone) {
		Activity activity = activityRepository.findByIdAndMemberId(activityId, memberId)
			.orElseThrow(ActivityNotFoundException::new);

		LocalDate activityDate = TimeZoneUtils.toLocalDate(activity.getStartedAt(), timeZone);
		return ActivityMapper.toStatisticsResult(activity, activityDate);
	}

	@Transactional(readOnly = true)
	public ActivityRecapResult getRecap(GetActivityRecapCommand command) {
		Activity activity = activityRepository.findByIdAndMemberId(command.activityId(), command.memberId())
			.orElseThrow(ActivityNotFoundException::new);

		return ActivityMapper.toRecapResult(
			activity,
			routeRepository.findByActivityIdOrderBySequenceAsc(command.activityId())
		);
	}

	@Transactional(readOnly = true)
	public ActivitiesByDateResult getActivitiesByDate(Long memberId, LocalDate date, ZoneId timeZone) {
		Instant startedAtFrom = TimeZoneUtils.toUtcInstant(date, timeZone);
		Instant startedAtTo = TimeZoneUtils.toUtcInstant(date.plusDays(1), timeZone).minusNanos(1);

		List<ActivityPreviewResult> activities = activityRepository
			.findByMemberIdAndActivityStatusAndStartedAtBetweenOrderByStartedAtAsc(
				memberId, ActivityStatus.ACTIVITY_COMPLETED, startedAtFrom, startedAtTo
			)
			.stream()
			.map(activity -> ActivityMapper.toActivityPreviewResult(activity, generateThumbnailUrl(activity)))
			.toList();

		return new ActivitiesByDateResult(date, activities);
	}

	@Transactional(readOnly = true)
	public ActivityEditListResult getActivityEditList(Long memberId, YearMonth yearMonth, ZoneId timeZone) {
		Instant startedAtFrom = TimeZoneUtils.toUtcInstant(yearMonth.atDay(1), timeZone);
		Instant startedAtTo = TimeZoneUtils.toUtcInstant(yearMonth.plusMonths(1).atDay(1), timeZone).minusNanos(1);

		List<Activity> activities = activityRepository
			.findByMemberIdAndActivityStatusAndStartedAtBetweenOrderByStartedAtAsc(
				memberId, ActivityStatus.ACTIVITY_COMPLETED, startedAtFrom, startedAtTo
			);

		if (activities.isEmpty()) {
			return new ActivityEditListResult(yearMonth.getYear(), yearMonth.getMonthValue(), List.of());
		}

		List<Long> activityIds = activities.stream().map(Activity::getId).toList();
		Map<Long, List<Timeline>> timelinesByActivityId = timelineRepository
			.findByActivityIdInAndTimelineStatusOrderByCreatedAtDesc(activityIds, TimelineStatus.SUCCESSFUL_CREATED)
			.stream()
			.collect(Collectors.groupingBy(timeline -> timeline.getActivity().getId()));

		List<ActivityEditItemResult> items = activities.stream()
			.map(activity -> {
				List<String> timelinePhotoUrls = timelinesByActivityId
					.getOrDefault(activity.getId(), List.of())
					.stream()
					.limit(MAX_EDIT_LIST_TIMELINE_PHOTO_COUNT)
					.map(timeline -> generateTimelineImageUrl(activity.getId(), timeline, FileUploadImageSize.MEDIUM))
					.toList();
				LocalDate activityDate = TimeZoneUtils.toLocalDate(activity.getStartedAt(), timeZone);
				return ActivityMapper.toActivityEditItemResult(activity, activityDate, timelinePhotoUrls);
			})
			.toList();

		return new ActivityEditListResult(yearMonth.getYear(), yearMonth.getMonthValue(), items);
	}

	@Transactional(readOnly = true)
	public ActivityTrackResult getTrack(Long activityId, Long memberId) {
		Activity activity = activityRepository.findByIdAndMemberId(activityId, memberId)
			.orElseThrow(ActivityNotFoundException::new);

		List<TrackPoint> trackPoints = ActivityTrackMapper.toTrackPoints(activity.getTrack());
		List<TrackPointResult> trackPointResults = trackPoints.stream()
			.map(ActivityTrackMapper::toTrackPointResult)
			.toList();

		List<Timeline> timelines = timelineRepository.findByActivityIdAndTimelineStatusOrderByTrackPointIndexAsc(
			activityId, TimelineStatus.SUCCESSFUL_CREATED
		);
		List<TimelineMarkerResult> timelineMarkers = timelines.stream()
			.map(timeline -> ActivityTrackMapper.toTimelineMarker(
				timeline, generateTimelineImageUrl(activityId, timeline, FileUploadImageSize.SMALL)
			))
			.toList();

		return new ActivityTrackResult(activityId, trackPointResults, timelineMarkers);
	}

	private String generateThumbnailUrl(Activity activity) {
		if (activity.getCoverImageObjectKey() == null) {
			return null;
		}

		FileImageAccessUrlCommand command = new FileImageAccessUrlCommand(
			FileUploadDirectory.TIMELINE,
			FileUploadImageSize.SMALL,
			activity.getId().toString(),
			activity.getCoverImageObjectKey()
		);
		return fileImageAccessUrlPort.generateImageUrl(command).imageUrl();
	}

	@Transactional
	public void deleteActivitiesByMemberId(long memberId) {
		activityRepository.deleteByMemberId(memberId);
	}

	private String generateTimelineImageUrl(Long activityId, Timeline timeline, FileUploadImageSize imageSize) {
		FileImageAccessUrlCommand command = new FileImageAccessUrlCommand(
			FileUploadDirectory.TIMELINE,
			imageSize,
			activityId.toString(),
			timeline.getTimelineImageObjectKey()
		);
		return fileImageAccessUrlPort.generateImageUrl(command).imageUrl();
	}
}
