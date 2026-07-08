package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.sopt.routee.activity.internal.exception.InvalidImageFileNameException;
import org.sopt.routee.activity.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileName;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;

class ActivityImageFileNameValidatorTest {

	private final ActivityImageFileNameValidator validator = new ActivityImageFileNameValidator();

	@Test
	void validate_파일명을_검증하고_정규화된_이름과_확장자를_반환한다() {
		ActivityImageFileName fileName = validator.validate("my hike.JPG");

		assertThat(fileName.sanitizedBaseName()).isEqualTo("my_hike");
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
	void validate_파일명에_슬래시가_있으면_예외를_던진다() {
		assertThatThrownBy(() -> validator.validate("dir/hike.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void validate_파일명에_역슬래시가_있으면_예외를_던진다() {
		assertThatThrownBy(() -> validator.validate("dir\\hike.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void validate_파일명에_제어문자가_있으면_예외를_던진다() {
		assertThatThrownBy(() -> validator.validate("hike\n.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
	}

	@Test
	void validate_정규화된_기본_파일명이_비어있으면_예외를_던진다() {
		assertThatThrownBy(() -> validator.validate("!!!.jpg"))
			.isInstanceOf(InvalidImageFileNameException.class);
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
