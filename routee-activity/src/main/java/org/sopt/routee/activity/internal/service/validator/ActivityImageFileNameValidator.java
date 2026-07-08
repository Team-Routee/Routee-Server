package org.sopt.routee.activity.internal.service.validator;

import java.util.Locale;
import java.util.Set;

import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.springframework.stereotype.Component;

@Component
public class ActivityImageFileNameValidator {

	private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "heic");

	public ActivityImageFileName validate(String fileName) {
		int extensionSeparatorIndex = fileName.lastIndexOf('.');
		if (extensionSeparatorIndex <= 0 || extensionSeparatorIndex == fileName.length() - 1) {
			throw new UnsupportedImageFileExtensionException();
		}

		String extension = fileName.substring(extensionSeparatorIndex + 1)
			.toLowerCase(Locale.ROOT);
		if (!SUPPORTED_EXTENSIONS.contains(extension)) {
			throw new UnsupportedImageFileExtensionException();
		}

		return new ActivityImageFileName(extension);
	}
}
