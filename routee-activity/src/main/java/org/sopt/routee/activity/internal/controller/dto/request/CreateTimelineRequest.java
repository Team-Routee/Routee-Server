package org.sopt.routee.activity.internal.controller.dto.request;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.timeline.TimelineStatus;
import org.sopt.routee.activity.internal.service.dto.command.CreateTimelineCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTimelineRequest(
	@Size(max = 16, message = "title은 16자 이하여야 합니다.")
	String title,

	@NotBlank(message = "timelineImageObjectKey는 필수입니다.")
	String timelineImageObjectKey,

	@NotNull(message = "createdAt은 필수입니다.")
	LocalDateTime createdAt,

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
			timelineImageObjectKey,
			createdAt,
			trackPointIndex,
			location.longitude(),
			location.latitude(),
			location.elevation(),
			location.pointIndex(),
			status,
			timeZone
		);
	}
}
