package org.sopt.routee.activity.internal.service.dto.result;

public record TrackPointResult(
	double latitude,
	double longitude,
	int elevation,
	int pointIndex
) {
}
