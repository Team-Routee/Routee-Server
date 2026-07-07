package org.sopt.routee.util;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class TimeZoneUtilsTest {

	@Test
	void toLocalDate_UTC_시각을_요청한_타임존의_날짜로_변환한다() {
		Instant instant = Instant.parse("2026-07-07T06:30:00Z");

		LocalDate seoulDate = TimeZoneUtils.toLocalDate(instant, ZoneId.of("Asia/Seoul"));
		LocalDate losAngelesDate = TimeZoneUtils.toLocalDate(instant, ZoneId.of("America/Los_Angeles"));

		assertThat(seoulDate).isEqualTo(LocalDate.of(2026, 7, 7));
		assertThat(losAngelesDate).isEqualTo(LocalDate.of(2026, 7, 6));
	}
}
