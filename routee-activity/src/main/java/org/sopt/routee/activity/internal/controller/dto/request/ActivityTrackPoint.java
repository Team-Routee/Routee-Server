package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.TrackPoint;

public record ActivityTrackPoint(
	double latitude,
	double longitude,
	int elevation,
	int pointIndex
) {
	public TrackPoint toTrackPoint(){
		return new TrackPoint(latitude, longitude, elevation, pointIndex);
	}
}
