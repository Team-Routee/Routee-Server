package org.sopt.routee.activity.internal.service.dto.result;

public record TimelineMarkerResult(
	Long timelineId,
	String thumbnailUrl,
	double latitude,
	double longitude,
	int pointIndex
) {
}
