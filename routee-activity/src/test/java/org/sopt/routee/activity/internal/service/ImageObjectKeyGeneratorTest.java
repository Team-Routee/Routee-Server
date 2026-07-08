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
	void generateStoredActivityImageKeyReturnsStoredObjectKey() {
		String objectKey = imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "my hike.JPG");

		assertThat(objectKey)
			.matches("100/[0-9a-f]{32}my_hike\\.jpg");
	}

	@Test
	void assembleOriginalActivityImageKeyReturnsFullObjectKey() {
		String objectKey = imageObjectKeyGenerator.assembleOriginalActivityImageKey("100/uuidmy_hike.jpg");

		assertThat(objectKey)
			.isEqualTo("activity/original/100/uuidmy_hike.jpg");
	}

	@Test
	void generateStoredActivityImageKeySupportsImageExtensions() {
		assertThat(imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike.jpeg"))
			.matches("100/[0-9a-f]{32}hike\\.jpeg");
		assertThat(imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike.png"))
			.matches("100/[0-9a-f]{32}hike\\.png");
		assertThat(imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike.webp"))
			.matches("100/[0-9a-f]{32}hike\\.webp");
		assertThat(imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike.heic"))
			.matches("100/[0-9a-f]{32}hike\\.heic");
	}

	@Test
	void generateStoredActivityImageKeyThrowsWhenFileNameContainsSlash() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "dir/hike.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateStoredActivityImageKeyThrowsWhenFileNameContainsBackslash() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "dir\\hike.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateStoredActivityImageKeyThrowsWhenFileNameContainsControlCharacter() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike\n.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateStoredActivityImageKeyThrowsWhenSanitizedBaseNameIsBlank() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "!!!.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void generateStoredActivityImageKeyThrowsWhenExtensionIsMissing() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, ".jpg"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike."))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
	}

	@Test
	void generateStoredActivityImageKeyThrowsWhenExtensionIsUnsupported() {
		assertThatThrownBy(() -> imageObjectKeyGenerator.generateStoredActivityImageKey(ACTIVITY_ID, "hike.gif"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
	}
}
