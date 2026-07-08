package org.sopt.routee.external.api.type;

public enum FileUploadDirectory {
	ACTIVITY("activity");

	private final String path;

	FileUploadDirectory(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
