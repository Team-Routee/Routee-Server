package org.sopt.routee.external.api.type;

public enum FileUploadImageSize {
	ORIGINAL("original"),
	SMALL("small"),
	MEDIUM("medium"),
	LARGE("large");

	private final String path;

	FileUploadImageSize(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
