package org.sopt.routee.activity.internal.service.dto.result;

import java.util.List;

public record ActivityTrackResult(
	Long activityId,
	List<TrackPointResult> trackPoints,
	List<TimelineMarkerResult> timelineMarkers
) {
}
