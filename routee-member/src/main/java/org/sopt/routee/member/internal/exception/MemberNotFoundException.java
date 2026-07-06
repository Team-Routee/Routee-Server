package org.sopt.routee.member.internal.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.member.internal.code.ErrorCode;

public class MemberNotFoundException extends BaseException {
	public MemberNotFoundException(){
		super(ErrorCode.MEMBER_NOT_FOUND);
	}
}
