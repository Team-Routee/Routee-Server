package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.UpdateNicknameCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NicknameUpdateRequest(
	@NotBlank
	@Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "닉네임은 한글, 영어, 숫자만 사용하여 2자 이상 12자 이하로 입력해야 합니다.")
	String nickname
) {
	public UpdateNicknameCommand toCommand(Long memberId) {
		return new UpdateNicknameCommand(memberId, nickname);
	}
}