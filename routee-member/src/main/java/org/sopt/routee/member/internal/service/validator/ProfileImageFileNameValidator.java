package org.sopt.routee.member.internal.service.validator;

import java.util.Set;

import org.sopt.routee.util.FileExtensionExtractor;
import org.springframework.stereotype.Component;

@Component
public class ProfileImageFileNameValidator {

	private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "heic");

	public boolean validate(String fileName) {
		return FileExtensionExtractor.extract(fileName)
			.map(SUPPORTED_EXTENSIONS::contains)
			.orElse(false);
	}
}