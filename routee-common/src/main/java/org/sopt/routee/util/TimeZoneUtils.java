package org.sopt.routee.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public final class TimeZoneUtils {

	private TimeZoneUtils() {
	}

	public static LocalDate toLocalDate(Instant instant, ZoneId zoneId) {
		return instant.atZone(zoneId).toLocalDate();
	}
}
