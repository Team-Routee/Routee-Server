package org.sopt.routee.activity.internal.mapper;

import java.time.Instant;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.exception.InvalidTrackException;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.activity.internal.service.dto.result.ActivityStatisticsResult;
import org.sopt.routee.activity.internal.service.dto.result.UpdateActivityStatusResult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityMapper {

	private static final int SRID = 4326;

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

	public static LineString toLineString(String track) {
		try {
			Geometry geometry = new WKTReader().read(track);
			if (!(geometry instanceof LineString lineString)) {
				throw new InvalidTrackException();
			}
			lineString.setSRID(SRID);
			return lineString;
		} catch (ParseException e) {
			throw new InvalidTrackException(e);
		}
	}
}
