package org.sopt.routee.member.internal.service.dto.command;

public record UpdateProfileImageCommand(
	Long memberId,
	String objectKey
) {
}
