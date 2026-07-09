package org.sopt.routee.activity.internal.service.dto.result;

public record TrackPointResult(
	double latitude,
	double longitude,
	Double elevation,
	int pointIndex
) {
}
