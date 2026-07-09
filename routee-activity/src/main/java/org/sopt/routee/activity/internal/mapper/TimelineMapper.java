package org.sopt.routee.activity.internal.mapper;

import java.time.Instant;

import org.locationtech.jts.geom.CoordinateXYZM;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.service.dto.command.CreateTimelineCommand;
import org.sopt.routee.activity.internal.service.dto.result.TimelineResult;

public class TimelineMapper {

	private static final int SRID = 4326;
	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), SRID);

	public static Timeline toEntity(CreateTimelineCommand command, Activity activity, Instant recordedAt) {
		return Timeline.builder()
			.title(command.title())
			.timelineImageObjectKey(command.objectKey())
			.createdAt(recordedAt)
			.trackPointIndex(command.trackPointIndex())
			.location(toPoint(command))
			.timelineStatus(command.status())
			.activity(activity)
			.build();
	}

	public static TimelineResult toTimelineResult(Timeline timeline) {
		return new TimelineResult(
			timeline.getId(),
			timeline.getTitle(),
			null,
			timeline.getCreatedAt()
		);
	}

	private static Point toPoint(CreateTimelineCommand command) {
		Point point = GEOMETRY_FACTORY.createPoint(new CoordinateXYZM(
			command.longitude(),
			command.latitude(),
			command.altitude(),
			command.measure()
		));
		point.setSRID(SRID);
		return point;
	}
}
