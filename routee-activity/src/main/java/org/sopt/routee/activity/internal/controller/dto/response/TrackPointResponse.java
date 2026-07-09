package org.sopt.routee.activity.internal.controller.dto.response;

import org.sopt.routee.activity.internal.service.dto.result.TrackPointResult;

public record TrackPointResponse(
	double latitude,
	double longitude,
	Double elevation,
	int pointIndex
) {
	public static TrackPointResponse from(TrackPointResult result) {
		return new TrackPointResponse(result.latitude(), result.longitude(), result.elevation(), result.pointIndex());
	}
}
