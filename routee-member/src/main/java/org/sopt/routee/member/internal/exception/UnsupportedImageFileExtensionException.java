package org.sopt.routee.member.internal.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.member.internal.code.ErrorCode;

public class UnsupportedImageFileExtensionException extends BaseException {
	public UnsupportedImageFileExtensionException() {
		super(ErrorCode.UNSUPPORTED_IMAGE_FILE_EXTENSION);
	}
}
