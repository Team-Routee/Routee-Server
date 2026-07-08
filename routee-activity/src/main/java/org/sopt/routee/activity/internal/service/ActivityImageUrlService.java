package org.sopt.routee.activity.internal.service;

import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityImageUrlService {

	private final ActivityRepository activityRepository;
	private final ImageObjectKeyGenerator imageObjectKeyGenerator;
	private final FileUploadPresignPort fileUploadPresignPort;

	@Transactional(readOnly = true)
	public ImageUrlResult generateImageUploadUrl(Long activityId, Long memberId, String fileName) {
		if (!activityRepository.existsByIdAndMemberId(activityId, memberId)) {
			throw new ActivityNotFoundException();
		}

		String objectKey = imageObjectKeyGenerator.generateStoredActivityImageKey(activityId, fileName);
		String presignedObjectKey = imageObjectKeyGenerator.assembleOriginalActivityImageKey(objectKey);
		String presignedUrl = fileUploadPresignPort.generatePutPresignedUrl(presignedObjectKey);

		return new ImageUrlResult(presignedUrl, objectKey);
	}
}
