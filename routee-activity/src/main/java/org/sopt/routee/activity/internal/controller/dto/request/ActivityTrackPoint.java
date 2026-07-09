package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.TrackPoint;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record ActivityTrackPoint(
	@DecimalMin(value = "-90", message = "latitude는 -90 이상이어야 합니다.")
	@DecimalMax(value = "90", message = "latitude는 90 이하여야 합니다.")
	double latitude,

	@DecimalMin(value = "-180", message = "longitude는 -180 이상이어야 합니다.")
	@DecimalMax(value = "180", message = "longitude는 180 이하여야 합니다.")
	double longitude,

	int elevation,
	int pointIndex
) {
	public TrackPoint toTrackPoint(){
		return new TrackPoint(latitude, longitude, elevation, pointIndex);
	}
}
