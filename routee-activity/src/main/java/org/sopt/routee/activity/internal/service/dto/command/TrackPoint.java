package org.sopt.routee.activity.internal.service.dto.command;

public record TrackPoint(
	double latitude,
	double longitude,
	int elevation,
	int pointIndex
) {
}
