package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileName;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;

class ActivityImageFileNameValidatorTest {

	private final ActivityImageFileNameValidator validator = new ActivityImageFileNameValidator();

	@Test
	void validate_파일명에서_확장자만_검증하고_반환한다() {
		ActivityImageFileName fileName = validator.validate("my hike.JPG");

		assertThat(fileName.extension()).isEqualTo("jpg");
	}

	@Test
	void validate_지원하는_이미지_확장자를_허용한다() {
		assertThat(validator.validate("hike.jpeg").extension()).isEqualTo("jpeg");
		assertThat(validator.validate("hike.png").extension()).isEqualTo("png");
		assertThat(validator.validate("hike.webp").extension()).isEqualTo("webp");
		assertThat(validator.validate("hike.heic").extension()).isEqualTo("heic");
	}

	@Test
	void validate_파일명은_검증하지_않고_확장자만_검증한다() {
		assertThat(validator.validate("dir/hike.jpg").extension()).isEqualTo("jpg");
		assertThat(validator.validate("dir\\hike.jpg").extension()).isEqualTo("jpg");
		assertThat(validator.validate("hike\n.jpg").extension()).isEqualTo("jpg");
		assertThat(validator.validate("!!!.jpg").extension()).isEqualTo("jpg");
	}

	@Test
	void validate_확장자가_없으면_예외를_던진다() {
		assertThatThrownBy(() -> validator.validate("hike"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
		assertThatThrownBy(() -> validator.validate(".jpg"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
		assertThatThrownBy(() -> validator.validate("hike."))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
	}

	@Test
	void validate_지원하지_않는_확장자면_예외를_던진다() {
		assertThatThrownBy(() -> validator.validate("hike.gif"))
			.isInstanceOf(UnsupportedImageFileExtensionException.class);
	}
}
