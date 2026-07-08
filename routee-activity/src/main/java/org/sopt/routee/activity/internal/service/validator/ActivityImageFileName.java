package org.sopt.routee.activity.internal.service.validator;

public record ActivityImageFileName(
	String sanitizedBaseName,
	String extension
) {
}
