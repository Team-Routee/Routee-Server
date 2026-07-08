package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.external.api.port.FileUploadPresignPort;

@ExtendWith(MockitoExtension.class)
class ActivityImageUrlServiceTest {

	private static final Long ACTIVITY_ID = 100L;
	private static final Long MEMBER_ID = 1L;

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private ImageObjectKeyGenerator imageObjectKeyGenerator;

	@Mock
	private FileUploadPresignPort fileUploadPresignPort;

	private ActivityImageUrlService activityImageUrlService;

	@BeforeEach
	void setUp() {
		activityImageUrlService = new ActivityImageUrlService(
			activityRepository,
			imageObjectKeyGenerator,
			fileUploadPresignPort
		);
	}

	@Test
	void generateImageUploadUrlReturnsPresignedUrlWithGeneratedObjectKey() {
		String objectKey = "100/uuidhike.jpg";
		String presignedObjectKey = "activity/original/100/uuidhike.jpg";

		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(true);
		when(imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike.jpg")).thenReturn(objectKey);
		when(imageObjectKeyGenerator.assembleOriginalActivityImageKey(objectKey)).thenReturn(presignedObjectKey);
		when(fileUploadPresignPort.generatePutPresignedUrl(presignedObjectKey)).thenReturn("https://presigned-url");

		ImageUrlResult result = activityImageUrlService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "hike.jpg");

		assertThat(result.presignedUrl()).isEqualTo("https://presigned-url");
		assertThat(result.objectKey()).isEqualTo(objectKey);
		verify(fileUploadPresignPort).generatePutPresignedUrl(presignedObjectKey);
	}

	@Test
	void generateImageUploadUrlThrowsWhenActivityDoesNotBelongToMember() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(false);

		assertThatThrownBy(() -> activityImageUrlService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "hike.jpg"))
			.isInstanceOf(ActivityNotFoundException.class);
	}
}
