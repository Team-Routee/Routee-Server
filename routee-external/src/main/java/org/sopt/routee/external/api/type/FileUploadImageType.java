package org.sopt.routee.external.api.type;

public enum FileUploadImageType {
	ORIGINAL("original"),
	RECAP("recap");

	private final String path;

	FileUploadImageType(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
