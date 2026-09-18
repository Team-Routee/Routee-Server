package org.sopt.routee.member.internal.service.dto.command;

public record ProfileImageUploadUrlCommand(
	Long memberId,
	String fileName
) {
}
