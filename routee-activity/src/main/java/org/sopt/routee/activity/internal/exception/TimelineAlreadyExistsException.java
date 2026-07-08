package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class TimelineAlreadyExistsException extends BaseException {
	public TimelineAlreadyExistsException() {
		super(ErrorCode.TIMELINE_ALREADY_EXISTS);
	}
}
