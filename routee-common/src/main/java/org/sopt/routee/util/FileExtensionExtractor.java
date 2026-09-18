package org.sopt.routee.util;

import java.util.Locale;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileExtensionExtractor {

	public static Optional<String> extract(String fileName) {
		int extensionSeparatorIndex = fileName.lastIndexOf('.');
		if (extensionSeparatorIndex <= 0 || extensionSeparatorIndex == fileName.length() - 1) {
			return Optional.empty();
		}

		return Optional.of(
			fileName.substring(extensionSeparatorIndex + 1).toLowerCase(Locale.ROOT)
		);
	}
}