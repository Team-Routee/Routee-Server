package org.sopt.routee.activity.internal.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class ActivityTimeUtils {

	private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
	private static final DateTimeFormatter TITLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	private ActivityTimeUtils() {
	}

	public static Instant nowUtc() {
		return Instant.now();
	}

	public static LocalDate toSeoulDate(Instant instant) {
		return instant.atZone(SEOUL_ZONE).toLocalDate();
	}

	public static String formatSeoulDateTitle(Instant instant) {
		return toSeoulDate(instant).format(TITLE_DATE_FORMATTER) + " 기록";
	}
}
