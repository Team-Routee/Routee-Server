package org.sopt.routee.activity.internal.service.validator;

import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ActivityImageFileNameValidator {

	private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "heic");

	public boolean validate(String fileName) {
		int extensionSeparatorIndex = fileName.lastIndexOf('.');
		if (extensionSeparatorIndex <= 0 || extensionSeparatorIndex == fileName.length() - 1) {
			return false;
		}

		String extension = fileName.substring(extensionSeparatorIndex + 1)
			.toLowerCase(Locale.ROOT);
		if (!SUPPORTED_EXTENSIONS.contains(extension)) {
			return false;
		}

		return true;
	}
}
