package org.sopt.routee.external.api.type;

public enum FileUploadImageSize {
	ORIGINAL("original"),
	RECAP("recap");

	private final String path;

	FileUploadImageSize(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
