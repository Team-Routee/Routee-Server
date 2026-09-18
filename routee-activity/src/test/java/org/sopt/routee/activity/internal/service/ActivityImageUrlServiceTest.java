package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.RouteRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.command.ImageUploadUrlCommand;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;
import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileUploadPresignResult;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class ActivityImageUrlServiceTest {

	private static final Long ACTIVITY_ID = 100L;
	private static final Long MEMBER_ID = 1L;
	private static final FileUploadDirectory DIRECTORY = FileUploadDirectory.TIMELINE;
	private static final FileUploadImageSize IMAGE_SIZE = FileUploadImageSize.ORIGINAL;

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private TimelineRepository timelineRepository;

	@Mock
	private ActivityDailySummaryService activityDailySummaryService;

	@Mock
	private ActivityImageFileNameValidator activityImageFileNameValidator;

	@Mock
	private FileUploadPresignPort fileUploadPresignPort;

	@Mock
	private FileImageAccessUrlPort fileImageAccessUrlPort;

	@Mock
	private FileDeletePort fileDeletePort;

	@Mock
	private RouteRepository routeRepository;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private TransactionTemplate transactionTemplate;

	private ActivityService activityService;

	@BeforeEach
	void setUp() {
		activityService = new ActivityService(
			activityRepository,
			timelineRepository,
			activityDailySummaryService,
			activityImageFileNameValidator,
			fileUploadPresignPort,
			fileImageAccessUrlPort,
			fileDeletePort,
			routeRepository,
			applicationEventPublisher,
			transactionTemplate
		);
	}

	@Test
	void generateImageUploadUrl_검증된_파일명으로_external에_presigned_url_발급을_요청한다() {
		String objectKey = "100/uuid.jpg";
		FileUploadPresignResult presignResult = new FileUploadPresignResult("https://presigned-url", objectKey);

		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(true);
		when(activityImageFileNameValidator.validate("hike.jpg")).thenReturn(true);
		FileUploadPresignCommand command = new FileUploadPresignCommand(
			DIRECTORY,
			IMAGE_SIZE,
			MEMBER_ID.toString(),
			ACTIVITY_ID.toString(),
			"hike.jpg"
		);
		when(fileUploadPresignPort.generatePutPresignedUrl(command))
			.thenReturn(presignResult);

		ImageUrlResult result = activityService.generateImageUploadUrl(
			new ImageUploadUrlCommand(ACTIVITY_ID, MEMBER_ID, "hike.jpg", DIRECTORY, IMAGE_SIZE)
		);

		assertThat(result.presignedUrl()).isEqualTo("https://presigned-url");
		assertThat(result.objectKey()).isEqualTo(objectKey);
		verify(fileUploadPresignPort).generatePutPresignedUrl(command);
	}

	@Test
	void generateImageUploadUrl_지원하지_않는_확장자면_예외를_던진다() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(true);
		when(activityImageFileNameValidator.validate("hike.gif")).thenReturn(false);

		assertThatThrownBy(() -> activityService.generateImageUploadUrl(
			new ImageUploadUrlCommand(ACTIVITY_ID, MEMBER_ID, "hike.gif", DIRECTORY, IMAGE_SIZE)
		))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
		verifyNoInteractions(fileUploadPresignPort);
	}

	@Test
	void generateImageUploadUrl_활동이_회원_소유가_아니면_예외를_던진다() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(false);

		assertThatThrownBy(() -> activityService.generateImageUploadUrl(
			new ImageUploadUrlCommand(ACTIVITY_ID, MEMBER_ID, "hike.jpg", DIRECTORY, IMAGE_SIZE)
		))
			.isInstanceOf(ActivityNotFoundException.class);
		verifyNoInteractions(activityImageFileNameValidator, fileUploadPresignPort);
	}
}
