package org.sopt.routee.activity.internal.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.AlreadyInProgressActivityException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.activity.internal.mapper.ActivityMapper;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.command.ImageUploadUrlCommand;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
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

	private static final DateTimeFormatter TITLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
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
		String title = activityDate.format(TITLE_DATE_FORMATTER) + " 기록";
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
}
