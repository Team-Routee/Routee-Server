package org.sopt.routee.activity.internal.mapper;

import java.time.Instant;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXYZM;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.route.Route;
import org.sopt.routee.activity.internal.exception.InvalidTrackException;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.command.TrackPoint;
import org.sopt.routee.activity.internal.service.dto.result.ActivityRecapResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityRecapRouteResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityStatisticsResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityPreviewResult;
import org.sopt.routee.activity.internal.service.dto.result.UpdateActivityStatusResult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityMapper {

	private static final int SRID = 4326;
	private static final int MIN_TRACK_POINT_COUNT = 2;
	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	public static Activity toEntity(CreateActivityCommand command, String title, Instant startedAt) {
		return Activity.builder()
			.title(title)
			.activityType(command.activityType())
			.activityStatus(ActivityStatus.ACTIVITY_IN_PROGRESS)
			.startedAt(startedAt)
			.memberId(command.memberId())
			.build();
	}

	public static UpdateActivityStatusResult toStatusUpdateResult(Activity activity) {
		return new UpdateActivityStatusResult(activity.getId(), activity.getActivityStatus());
	}

	public static ActivityStatisticsResult toStatisticsResult(Activity activity, String activityDate) {
		return new ActivityStatisticsResult(
			activity.getId(),
			activity.getTitle(),
			activityDate,
			activity.getDistance(),
			activity.getDurationSec(),
			activity.getMaxElevation()
		);
	}

	public static ActivityPreviewResult toActivityPreviewResult(Activity activity, String thumbnailUrl) {
		return new ActivityPreviewResult(activity.getId(), activity.getTitle(), thumbnailUrl);
	}

	public static ActivityRecapResult toRecapResult(Activity activity, List<Route> routes) {
		List<ActivityRecapRouteResult> routeResults = routes.stream()
			.map(route -> new ActivityRecapRouteResult(route.getSequence(), route.getName()))
			.toList();

		return new ActivityRecapResult(
			activity.getDistance(),
			activity.getDurationSec(),
			activity.getMaxElevation(),
			activity.getMapImageUrl(),
			routeResults
		);
	}

	public static LineString toLineString(List<TrackPoint> trackPoints) {
		if (trackPoints == null || trackPoints.size() < MIN_TRACK_POINT_COUNT) {
			throw new InvalidTrackException();
		}

		Coordinate[] coordinates = trackPoints.stream()
			.map(point -> (Coordinate) new CoordinateXYZM(
				point.longitude(),
				point.latitude(),
				point.elevation(),
				point.pointIndex()
			))
			.toArray(Coordinate[]::new);

		LineString lineString = GEOMETRY_FACTORY.createLineString(coordinates);
		lineString.setSRID(SRID);
		return lineString;
	}
}
