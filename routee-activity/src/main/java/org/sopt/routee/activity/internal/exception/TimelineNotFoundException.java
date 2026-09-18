package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class TimelineNotFoundException extends BaseException {

	public TimelineNotFoundException() {
		super(ErrorCode.TIMELINE_NOT_FOUND);
	}
}
