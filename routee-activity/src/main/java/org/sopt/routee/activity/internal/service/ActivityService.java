package org.sopt.routee.activity.internal.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.exception.ActivityAlreadyCompletedException;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.ActivityStatusAlreadySameException;
import org.sopt.routee.activity.internal.exception.AlreadyInProgressActivityException;
import org.sopt.routee.activity.internal.exception.InvalidActivityStatusTransitionException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.activity.internal.mapper.ActivityMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.command.CompleteActivityCommand;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.command.ImageUploadUrlCommand;
import org.sopt.routee.activity.internal.service.dto.command.UpdateActivityStatusCommand;
import org.sopt.routee.activity.internal.service.dto.result.ActivityStatisticsResult;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.activity.internal.service.dto.result.UpdateActivityStatusResult;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;
import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileUploadPresignResult;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;
import org.sopt.routee.util.TimeZoneUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	private static final Set<ActivityStatus> ACTIVE_STATUSES = EnumSet.of(
		ActivityStatus.ACTIVITY_IN_PROGRESS,
		ActivityStatus.ACTIVITY_PAUSED
	);

	private final ActivityRepository activityRepository;
	private final ActivityImageFileNameValidator activityImageFileNameValidator;
	private final FileUploadPresignPort fileUploadPresignPort;

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
		String title = activityDate.format(DATE_FORMATTER) + " 기록";
		Activity activity = ActivityMapper.toEntity(command, title, startedAt);
		Activity savedActivity = activityRepository.save(activity);
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
			FileUploadDirectory.ACTIVITY,
			FileUploadImageSize.ORIGINAL,
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
	}

	@Transactional(readOnly = true)
	public ActivityStatisticsResult getStatistics(Long activityId, Long memberId, ZoneId timeZone) {
		Activity activity = activityRepository.findByIdAndMemberId(activityId, memberId)
			.orElseThrow(ActivityNotFoundException::new);

		LocalDate activityDate = TimeZoneUtils.toLocalDate(activity.getStartedAt(), timeZone);
		return ActivityMapper.toStatisticsResult(activity, activityDate.format(DATE_FORMATTER));
	}
}
