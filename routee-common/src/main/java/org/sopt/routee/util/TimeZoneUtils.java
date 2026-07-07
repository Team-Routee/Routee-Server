package org.sopt.routee.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeZoneUtils {

	public static LocalDate toLocalDate(Instant instant, ZoneId zoneId) {
		return instant.atZone(zoneId).toLocalDate();
	}

	public static LocalDateTime toLocalDateTime(Instant instant, ZoneId zoneId) {
		return instant.atZone(zoneId).toLocalDateTime();
	}

	public static Instant toUtcInstant(LocalDate localDate, ZoneId zoneId) {
		return localDate.atStartOfDay(zoneId).toInstant();
	}

	public static Instant toUtcInstant(LocalDateTime localDateTime, ZoneId zoneId) {
		return localDateTime.atZone(zoneId).toInstant();
	}
}
