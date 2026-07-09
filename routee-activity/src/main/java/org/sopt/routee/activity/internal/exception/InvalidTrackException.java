package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class InvalidTrackException extends BaseException {

	public InvalidTrackException() {
		super(ErrorCode.INVALID_TRACK);
	}

	public InvalidTrackException(Throwable cause) {
		super(ErrorCode.INVALID_TRACK, cause);
	}
}
