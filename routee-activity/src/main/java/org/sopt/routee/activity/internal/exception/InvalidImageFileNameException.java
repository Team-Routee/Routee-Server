package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class InvalidImageFileNameException extends BaseException {

	public InvalidImageFileNameException() {
		super(ErrorCode.INVALID_IMAGE_FILE_NAME);
	}
}
