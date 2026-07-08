package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileName;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;
import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileUploadPresignResult;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;

@ExtendWith(MockitoExtension.class)
class ActivityImageUrlServiceTest {

	private static final Long ACTIVITY_ID = 100L;
	private static final Long MEMBER_ID = 1L;

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private ActivityImageFileNameValidator activityImageFileNameValidator;

	@Mock
	private FileUploadPresignPort fileUploadPresignPort;

	private ActivityService activityService;

	@BeforeEach
	void setUp() {
		activityService = new ActivityService(
			activityRepository,
			activityImageFileNameValidator,
			fileUploadPresignPort
		);
	}

	@Test
	void generateImageUploadUrl_검증된_파일명으로_external에_presigned_url_발급을_요청한다() {
		String objectKey = "100/uuidhike.jpg";
		ActivityImageFileName fileName = new ActivityImageFileName("hike", "jpg");
		FileUploadPresignResult presignResult = new FileUploadPresignResult("https://presigned-url", objectKey);

		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(true);
		when(activityImageFileNameValidator.validate("hike.jpg")).thenReturn(fileName);
		FileUploadPresignCommand command = new FileUploadPresignCommand(
			FileUploadDirectory.ACTIVITY,
			FileUploadImageSize.ORIGINAL,
			ACTIVITY_ID.toString(),
			"hike",
			"jpg"
		);
		when(fileUploadPresignPort.generatePutPresignedUrl(command))
			.thenReturn(presignResult);

		ImageUrlResult result = activityService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "hike.jpg");

		assertThat(result.presignedUrl()).isEqualTo("https://presigned-url");
		assertThat(result.objectKey()).isEqualTo(objectKey);
		verify(fileUploadPresignPort).generatePutPresignedUrl(command);
	}

	@Test
	void generateImageUploadUrl_활동이_회원_소유가_아니면_예외를_던진다() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(false);

		assertThatThrownBy(() -> activityService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "hike.jpg"))
			.isInstanceOf(ActivityNotFoundException.class);
		verifyNoInteractions(activityImageFileNameValidator, fileUploadPresignPort);
	}
}
