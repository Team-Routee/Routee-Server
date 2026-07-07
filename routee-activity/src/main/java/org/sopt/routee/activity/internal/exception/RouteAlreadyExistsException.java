package org.sopt.routee.activity.internal.exception;

import org.sopt.routee.activity.internal.code.ErrorCode;
import org.sopt.routee.exception.BaseException;

public class RouteAlreadyExistsException extends BaseException {
	public RouteAlreadyExistsException() {
		super(ErrorCode.ROUTE_ALREADY_EXISTS);
	}
}
