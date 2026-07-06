package org.sopt.routee.member.internal.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.member.internal.code.ErrorCode;

public class AlreadyRegisteredMemberException extends BaseException {

	public AlreadyRegisteredMemberException() {
		super(ErrorCode.ALREADY_REGISTERED_MEMBER);
	}
}