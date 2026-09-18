package org.sopt.routee.external.api.type;

public enum FileUploadDirectory {
	TIMELINE("timeline"),
	RECAP("recap"),
	PROFILE("profile");

	private final String path;

	FileUploadDirectory(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
