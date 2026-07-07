package org.sopt.routee.activity.internal.service;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.InvalidImageFileNameException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityImageUrlService {

	private static final String ORIGINAL_SIZE = "original";
	private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "heic");
	private static final Pattern UNSAFE_FILE_NAME = Pattern.compile("[/\\\\\\p{Cntrl}]");
	private static final Pattern UNSAFE_BASE_NAME_CHARACTER = Pattern.compile("[^A-Za-z0-9._-]");

	private final ActivityRepository activityRepository;
	private final FileUploadPresignPort fileUploadPresignPort;

	@Transactional(readOnly = true)
	public ImageUrlResult generateImageUploadUrl(Long activityId, Long memberId, String fileName) {
		if (!activityRepository.existsByIdAndMemberId(activityId, memberId)) {
			throw new ActivityNotFoundException();
		}

		String objectKey = createObjectKey(activityId, fileName);
		String presignedUrl = fileUploadPresignPort.generatePutPresignedUrl(objectKey);

		return new ImageUrlResult(presignedUrl, objectKey);
	}

	private String createObjectKey(Long activityId, String fileName) {
		validateFileName(fileName);

		int extensionSeparatorIndex = fileName.lastIndexOf('.');
		if (extensionSeparatorIndex <= 0 || extensionSeparatorIndex == fileName.length() - 1) {
			throw new UnsupportedImageFileExtensionException();
		}

		String extension = fileName.substring(extensionSeparatorIndex + 1)
			.toLowerCase(Locale.ROOT);
		if (!SUPPORTED_EXTENSIONS.contains(extension)) {
			throw new UnsupportedImageFileExtensionException();
		}

		String sanitizedBaseName = sanitizeBaseName(fileName.substring(0, extensionSeparatorIndex));
		String uuid = UUID.randomUUID().toString().replace("-", "");

		return "activities/%d/images/%s/%s_%s.%s"
			.formatted(activityId, ORIGINAL_SIZE, uuid, sanitizedBaseName, extension);
	}

	private void validateFileName(String fileName) {
		if (UNSAFE_FILE_NAME.matcher(fileName).find()) {
			throw new InvalidImageFileNameException();
		}
	}

	private String sanitizeBaseName(String baseName) {
		String sanitizedBaseName = UNSAFE_BASE_NAME_CHARACTER.matcher(baseName.trim())
			.replaceAll("_")
			.replaceAll("_+", "_")
			.replaceAll("^_+|_+$", "");

		if (sanitizedBaseName.isBlank()) {
			throw new InvalidImageFileNameException();
		}

		return sanitizedBaseName;
	}
}
