package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.sopt.routee.activity.internal.service.validator.ActivityImageFileNameValidator;

class ActivityImageFileNameValidatorTest {

	private final ActivityImageFileNameValidator validator = new ActivityImageFileNameValidator();

	@Test
	void validate_지원하는_이미지_확장자면_true를_반환한다() {
		assertThat(validator.validate("my hike.JPG")).isTrue();
	}

	@Test
	void validate_지원하는_이미지_확장자를_허용한다() {
		assertThat(validator.validate("hike.jpeg")).isTrue();
		assertThat(validator.validate("hike.png")).isTrue();
		assertThat(validator.validate("hike.webp")).isTrue();
		assertThat(validator.validate("hike.heic")).isTrue();
	}

	@Test
	void validate_파일명은_검증하지_않고_확장자만_검증한다() {
		assertThat(validator.validate("dir/hike.jpg")).isTrue();
		assertThat(validator.validate("dir\\hike.jpg")).isTrue();
		assertThat(validator.validate("hike\n.jpg")).isTrue();
		assertThat(validator.validate("!!!.jpg")).isTrue();
	}

	@Test
	void validate_확장자가_없으면_false를_반환한다() {
		assertThat(validator.validate("hike")).isFalse();
		assertThat(validator.validate(".jpg")).isFalse();
		assertThat(validator.validate("hike.")).isFalse();
	}

	@Test
	void validate_지원하지_않는_확장자면_false를_반환한다() {
		assertThat(validator.validate("hike.gif")).isFalse();
	}
}
