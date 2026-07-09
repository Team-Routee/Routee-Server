package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class ActivityAlreadyCompletedException extends BaseException {
	public ActivityAlreadyCompletedException() {
		super(ErrorCode.ACTIVITY_ALREADY_COMPLETED);
	}
}
