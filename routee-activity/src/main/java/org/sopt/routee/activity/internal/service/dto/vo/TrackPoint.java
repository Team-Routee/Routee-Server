package org.sopt.routee.activity.internal.service.dto.vo;

public record TrackPoint(
	double latitude,
	double longitude,
	int elevation,
	int pointIndex
) {
}
