package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class ActivityNotFoundException extends BaseException {

	public ActivityNotFoundException() {
		super(ErrorCode.ACTIVITY_NOT_FOUND);
	}
}
