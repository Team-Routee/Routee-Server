package org.sopt.routee.activity.internal.controller.dto.request;

import org.sopt.routee.activity.internal.service.dto.command.UpdateActivityTitleCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivityTitleUpdateRequest(
	@NotBlank(message = "title은 필수입니다.")
	@Size(max = 16, message = "title은 16자 이하여야 합니다.")
	String title
) {
	public UpdateActivityTitleCommand toCommand(Long activityId, Long memberId) {
		return new UpdateActivityTitleCommand(activityId, memberId, title);
	}
}
