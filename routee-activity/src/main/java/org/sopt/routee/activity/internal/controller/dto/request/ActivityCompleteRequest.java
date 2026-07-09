package org.sopt.routee.activity.internal.controller.dto.request;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.activity.ActivityType;
import org.sopt.routee.activity.internal.service.dto.command.CompleteActivityCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActivityCompleteRequest(
	@NotBlank(message = "title은 필수입니다.")
	String title,

	@NotNull(message = "activityType은 필수입니다.")
	ActivityType activityType,

	@NotNull(message = "status는 필수입니다.")
	ActivityStatus status,

	@NotNull(message = "distance는 필수입니다.")
	Integer distance,

	@NotNull(message = "durationSec은 필수입니다.")
	Integer durationSec,

	@NotNull(message = "maxElevation은 필수입니다.")
	Integer maxElevation,

	@NotBlank(message = "mapImageUrl은 필수입니다.")
	String mapImageUrl,

	@NotBlank(message = "coverImageObjectKey는 필수입니다.")
	String coverImageObjectKey,

	@NotBlank(message = "track은 필수입니다.")
	String track,

	@NotNull(message = "startedAt은 필수입니다.")
	LocalDateTime startedAt,

	@NotNull(message = "endedAt은 필수입니다.")
	LocalDateTime endedAt
) {
	public CompleteActivityCommand toCommand(Long activityId, Long memberId, ZoneId timeZone) {
		return new CompleteActivityCommand(
			activityId,
			memberId,
			timeZone,
			title,
			activityType,
			status,
			distance,
			durationSec,
			maxElevation,
			mapImageUrl,
			coverImageObjectKey,
			track,
			startedAt,
			endedAt
		);
	}
}
