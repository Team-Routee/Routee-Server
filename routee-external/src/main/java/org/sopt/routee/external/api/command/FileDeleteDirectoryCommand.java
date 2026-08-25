package org.sopt.routee.external.api.command;

public record FileDeleteDirectoryCommand(
	String memberId,
	String activityId
) {
}
