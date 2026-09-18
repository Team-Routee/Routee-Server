package org.sopt.routee.member.internal.service.dto.command;

public record UpdateNicknameCommand(
	Long memberId,
	String nickname
) {
}
