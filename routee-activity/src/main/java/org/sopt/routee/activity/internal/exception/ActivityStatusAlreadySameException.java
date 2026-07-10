package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class ActivityStatusAlreadySameException extends BaseException {
	public ActivityStatusAlreadySameException() {
		super(ErrorCode.ACTIVITY_STATUS_ALREADY_SAME);
	}
}
