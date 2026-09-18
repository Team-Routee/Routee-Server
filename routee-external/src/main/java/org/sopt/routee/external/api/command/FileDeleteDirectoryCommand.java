package org.sopt.routee.external.api.command;

public record FileDeleteDirectoryCommand(
	String memberId,
	String activityId
) {

	public FileDeleteDirectoryCommand(String memberId) {
		this(memberId, null);
	}
}
