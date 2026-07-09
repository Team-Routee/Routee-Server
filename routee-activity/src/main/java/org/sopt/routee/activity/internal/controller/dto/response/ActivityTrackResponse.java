package org.sopt.routee.activity.internal.controller.dto.response;

import java.util.List;

import org.sopt.routee.activity.internal.service.dto.result.ActivityTrackResult;

public record ActivityTrackResponse(
	Long activityId,
	List<TrackPointResponse> trackPoints,
	List<TimelineMarkerResponse> timelineMarkers
) {
	public static ActivityTrackResponse from(ActivityTrackResult result) {
		return new ActivityTrackResponse(
			result.activityId(),
			result.trackPoints().stream().map(TrackPointResponse::from).toList(),
			result.timelineMarkers().stream().map(TimelineMarkerResponse::from).toList()
		);
	}
}
