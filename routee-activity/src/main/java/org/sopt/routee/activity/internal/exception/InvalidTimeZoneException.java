package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class InvalidTimeZoneException extends BaseException {

	public InvalidTimeZoneException() {
		super(ErrorCode.INVALID_TIME_ZONE);
	}
}
