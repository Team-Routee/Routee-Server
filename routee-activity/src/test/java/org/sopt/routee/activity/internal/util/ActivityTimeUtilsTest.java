package org.sopt.routee.activity.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ActivityTimeUtilsTest {

	@Test
	void toSeoulDate_UTC_시각을_서울_날짜로_변환한다() {
		Instant instant = Instant.parse("2026-07-06T15:00:00Z");

		LocalDate seoulDate = ActivityTimeUtils.toSeoulDate(instant);

		assertThat(seoulDate).isEqualTo(LocalDate.of(2026, 7, 7));
	}

	@Test
	void formatSeoulDateTitle_서울_날짜_기준_기본_제목을_반환한다() {
		Instant instant = Instant.parse("2026-07-06T15:00:00Z");

		String title = ActivityTimeUtils.formatSeoulDateTitle(instant);

		assertThat(title).isEqualTo("2026.07.07 기록");
	}
}
