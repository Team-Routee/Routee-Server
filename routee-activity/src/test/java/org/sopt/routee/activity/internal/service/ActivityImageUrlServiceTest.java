package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.InvalidImageFileNameException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
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
	private FileUploadPresignPort fileUploadPresignPort;

	private ActivityImageUrlService activityImageUrlService;

	@BeforeEach
	void setUp() {
		activityImageUrlService = new ActivityImageUrlService(activityRepository, fileUploadPresignPort);
	}

	@Test
	void generateImageUploadUrlReturnsOriginalObjectKey() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(true);
		when(fileUploadPresignPort.generatePutPresignedUrl(any())).thenReturn("https://presigned-url");

		ImageUrlResult result = activityImageUrlService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "my hike.JPG");

		assertThat(result.presignedUrl()).isEqualTo("https://presigned-url");
		assertThat(result.objectKey())
			.matches("activities/100/images/original/[0-9a-f]{32}_my_hike\\.jpg");
	}

	@Test
	void generateImageUploadUrlThrowsWhenActivityDoesNotBelongToMember() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(false);

		assertThatThrownBy(() -> activityImageUrlService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "hike.jpg"))
			.isInstanceOf(ActivityNotFoundException.class);
	}

	@Test
	void generateImageUploadUrlThrowsWhenFileNameContainsSlash() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(true);

		assertThatThrownBy(() -> activityImageUrlService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "dir/hike.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateImageUploadUrlThrowsWhenExtensionIsUnsupported() {
		when(activityRepository.existsByIdAndMemberId(ACTIVITY_ID, MEMBER_ID)).thenReturn(true);

		assertThatThrownBy(() -> activityImageUrlService.generateImageUploadUrl(ACTIVITY_ID, MEMBER_ID, "hike.gif"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
	}
}
