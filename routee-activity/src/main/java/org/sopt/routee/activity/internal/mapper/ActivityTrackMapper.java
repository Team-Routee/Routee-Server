package org.sopt.routee.activity.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.service.dto.command.TrackPoint;
import org.sopt.routee.activity.internal.service.dto.result.TimelineMarkerResult;
import org.sopt.routee.activity.internal.service.dto.result.TrackPointResult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityTrackMapper {

	public static List<TrackPoint> toTrackPoints(LineString track) {
		if (track == null) {
			return List.of();
		}

		CoordinateSequence sequence = track.getCoordinateSequence();
		List<TrackPoint> trackPoints = new ArrayList<>(sequence.size());
		for (int i = 0; i < sequence.size(); i++) {
			double elevation = sequence.getZ(i);
			trackPoints.add(new TrackPoint(
				sequence.getY(i),
				sequence.getX(i),
				Double.isNaN(elevation) ? null : elevation,
				(int) sequence.getM(i)
			));
		}
		return trackPoints;
	}

	public static TrackPointResult toTrackPointResult(TrackPoint trackPoint) {
		return new TrackPointResult(
			trackPoint.latitude(),
			trackPoint.longitude(),
			trackPoint.elevation(),
			trackPoint.pointIndex()
		);
	}

	public static TimelineMarkerResult toTimelineMarker(Timeline timeline, String thumbnailUrl) {
		Point location = timeline.getLocation();
		return new TimelineMarkerResult(
			timeline.getId(),
			thumbnailUrl,
			location.getY(),
			location.getX()
		);
	}
}
