package org.sopt.routee.activity.internal.controller.dto.request;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.sopt.routee.activity.internal.service.dto.command.CompleteActivityCommand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ActivityCompleteRequest(
	@NotBlank(message = "title은 필수입니다.")
	@Size(max = 16, message = "title은 16자 이하여야 합니다.")
	String title,

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

	String coverImageObjectKey,

	@NotEmpty(message = "track은 필수입니다.")
	@Valid
	List<ActivityTrackPoint> track,

	@NotNull(message = "endedAt은 필수입니다.")
	LocalDateTime endedAt
) {
	public CompleteActivityCommand toCommand(Long activityId, Long memberId, ZoneId timeZone) {
		return new CompleteActivityCommand(
			activityId,
			memberId,
			timeZone,
			title,
			distance,
			durationSec,
			maxElevation,
			mapImageUrl,
			coverImageObjectKey,
			track.stream().map(ActivityTrackPoint::toTrackPoint).toList(),
			endedAt
		);
	}
}
