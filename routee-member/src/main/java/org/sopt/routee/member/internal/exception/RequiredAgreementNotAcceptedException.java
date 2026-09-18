package org.sopt.routee.member.internal.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.member.internal.code.ErrorCode;

public class RequiredAgreementNotAcceptedException extends BaseException {

	public RequiredAgreementNotAcceptedException() {
		super(ErrorCode.REQUIRED_AGREEMENT_NOT_ACCEPTED);
	}
}
