package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class AlreadyInProgressActivityException extends BaseException {

	public AlreadyInProgressActivityException() {
		super(ErrorCode.ALREADY_IN_PROGRESS_ACTIVITY);
	}
}
