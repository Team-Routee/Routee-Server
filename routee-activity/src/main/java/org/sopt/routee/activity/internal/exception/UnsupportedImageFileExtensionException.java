package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class UnsupportedImageFileExtensionException extends BaseException {

	public UnsupportedImageFileExtensionException() {
		super(ErrorCode.UNSUPPORTED_IMAGE_FILE_EXTENSION);
	}
}
