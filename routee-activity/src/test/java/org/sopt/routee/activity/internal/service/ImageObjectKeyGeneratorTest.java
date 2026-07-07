package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.sopt.routee.activity.internal.exception.InvalidImageFileNameException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;

class ImageObjectKeyGeneratorTest {

	private static final Long ACTIVITY_ID = 100L;

	private final ImageObjectKeyGenerator imageObjectKeyGenerator = new ImageObjectKeyGenerator();

	@Test
	void generateOriginalActivityImageKeyReturnsOriginalObjectKey() {
		String objectKey = imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "my hike.JPG");

		assertThat(objectKey)
			.matches("activities/100/images/original/[0-9a-f]{32}_my_hike\\.jpg");
	}

	@Test
	void generateOriginalActivityImageKeySupportsImageExtensions() {
		assertThat(imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike.jpeg"))
			.matches("activities/100/images/original/[0-9a-f]{32}_hike\\.jpeg");
		assertThat(imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike.png"))
			.matches("activities/100/images/original/[0-9a-f]{32}_hike\\.png");
		assertThat(imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike.webp"))
			.matches("activities/100/images/original/[0-9a-f]{32}_hike\\.webp");
		assertThat(imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike.heic"))
			.matches("activities/100/images/original/[0-9a-f]{32}_hike\\.heic");
	}

	@Test
	void generateOriginalActivityImageKeyThrowsWhenFileNameContainsSlash() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "dir/hike.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateOriginalActivityImageKeyThrowsWhenFileNameContainsBackslash() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "dir\\hike.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateOriginalActivityImageKeyThrowsWhenFileNameContainsControlCharacter() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike\n.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateOriginalActivityImageKeyThrowsWhenSanitizedBaseNameIsBlank() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "!!!.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateOriginalActivityImageKeyThrowsWhenExtensionIsMissing() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, ".jpg"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike."))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
	}

	@Test
	void generateOriginalActivityImageKeyThrowsWhenExtensionIsUnsupported() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateOriginalActivityImageKey(ACTIVITY_ID, "hike.gif"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
	}
}
