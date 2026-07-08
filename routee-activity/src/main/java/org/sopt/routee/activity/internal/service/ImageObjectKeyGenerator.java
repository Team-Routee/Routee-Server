package org.sopt.routee.activity.internal.service;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.sopt.routee.activity.internal.exception.InvalidImageFileNameException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.springframework.stereotype.Component;

@Component
public class ImageObjectKeyGenerator {

	private static final String ORIGINAL_SIZE = "original";
	private static final String ACTIVITY_PREFIX = "activity";
	private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "heic");
	private static final Pattern UNSAFE_FILE_NAME = Pattern.compile("[/\\\\\\p{Cntrl}]");
	private static final Pattern UNSAFE_BASE_NAME_CHARACTER = Pattern.compile("[^A-Za-z0-9._-]");

	public String generateStoredActivityImageKey(Long activityId, String fileName) {
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

		return "%d/%s%s.%s"
			.formatted(activityId, uuid, sanitizedBaseName, extension);
	}

	public String assembleOriginalActivityImageKey(String storedObjectKey) {
		return "%s/%s/%s".formatted(ACTIVITY_PREFIX, ORIGINAL_SIZE, storedObjectKey);
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
