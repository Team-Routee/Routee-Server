package org.sopt.routee.activity.internal.controller.dto.request;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.activity.ActivityType;
import org.sopt.routee.activity.internal.service.dto.command.CompleteActivityCommand;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ActivityCompleteRequest(
	@NotBlank(message = "title은 필수입니다.")
	@Size(max = 16, message = "title은 16자 이하여야 합니다.")
	String title,

	@NotNull(message = "activityType은 필수입니다.")
	ActivityType activityType,

	@NotNull(message = "status는 필수입니다.")
	ActivityStatus status,

	@NotNull(message = "distance는 필수입니다.")
	@PositiveOrZero(message = "distance는 0 이상이어야 합니다.")
	Integer distance,

	@NotNull(message = "durationSec은 필수입니다.")
	@Positive(message = "durationSec은 1 이상이어야 합니다.")
	Integer durationSec,

	@NotNull(message = "maxElevation은 필수입니다.")
	@Min(value = -500, message = "maxElevation은 -500 이상이어야 합니다.")
	@Max(value = 9000, message = "maxElevation은 9000 이하여야 합니다.")
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
	@AssertTrue(message = "endedAt은 startedAt에 durationSec을 더한 시간 이후여야 합니다.")
	public boolean isValidActivityTimeRange() {
		if (startedAt == null || endedAt == null || durationSec == null) {
			return true;
		}
		return !endedAt.isBefore(startedAt.plusSeconds(durationSec.longValue()));
	}

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
