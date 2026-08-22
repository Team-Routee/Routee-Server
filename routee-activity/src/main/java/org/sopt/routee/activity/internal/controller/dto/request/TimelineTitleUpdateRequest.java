package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.UpdateTimelineTitleCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TimelineTitleUpdateRequest(
	@NotBlank(message = "title은 필수입니다.")
	@Size(max = 16, message = "title은 16자 이하여야 합니다.")
	String title
) {
	public UpdateTimelineTitleCommand toCommand(Long activityId, Long timelineId, Long memberId) {
		return new UpdateTimelineTitleCommand(activityId, timelineId, memberId, title);
	}
}
