package org.sopt.routee.converter;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ZoneIdConverter implements Converter<String, ZoneId> {

	public ZoneId convert(@NonNull String source) {
		try {
			ZoneId zoneId = ZoneId.of(source);

			if (zoneId instanceof ZoneOffset) {
				throw new IllegalArgumentException();
			}

			return zoneId;
		} catch (DateTimeException e) {
			throw new IllegalArgumentException();
		}
	}
}
