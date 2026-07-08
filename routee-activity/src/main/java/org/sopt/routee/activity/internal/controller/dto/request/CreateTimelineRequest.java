package org.sopt.routee.activity.internal.controller.dto.request;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;
import org.sopt.routee.activity.internal.service.dto.command.CreateTimelineCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTimelineRequest(
	@NotBlank(message = "title은 필수입니다.")
	String title,

	@NotBlank(message = "objectKey는 필수입니다.")
	String objectKey,

	@NotNull(message = "recordedAt은 필수입니다.")
	LocalDateTime recordedAt,

	@NotNull(message = "trackPointIndex는 필수입니다.")
	Integer trackPointIndex,

	@Valid
	@NotNull(message = "location은 필수입니다.")
	TimelineLocationRequest location,

	@NotNull(message = "status는 필수입니다.")
	TimelineStatus status
) {
	public CreateTimelineCommand toCommand(Long memberId, Long activityId, ZoneId timeZone) {
		return new CreateTimelineCommand(
			memberId,
			activityId,
			title,
			objectKey,
			recordedAt,
			trackPointIndex,
			location.longitude(),
			location.latitude(),
			location.altitude(),
			location.measure(),
			status,
			timeZone
		);
	}
}
