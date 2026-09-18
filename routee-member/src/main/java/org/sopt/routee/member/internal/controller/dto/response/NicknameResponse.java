package org.sopt.routee.member.internal.controller.dto.response;

import org.sopt.routee.member.internal.service.dto.result.UpdateNicknameResult;

public record NicknameResponse(
	String nickname
) {
	public static NicknameResponse from(UpdateNicknameResult result) {
		return new NicknameResponse(result.nickname());
	}
}