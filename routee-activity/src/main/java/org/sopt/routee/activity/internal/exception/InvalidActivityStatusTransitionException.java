package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class InvalidActivityStatusTransitionException extends BaseException {
	public InvalidActivityStatusTransitionException() {
		super(ErrorCode.INVALID_ACTIVITY_STATUS_TRANSITION);
	}
}
