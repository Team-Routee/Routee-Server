package org.sopt.routee.external.internal.s3.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.s3.code.ErrorCode;

public final class FileDeleteException extends BaseException {

	public FileDeleteException(Throwable cause) {
		super(ErrorCode.FILE_DELETE_FAILED, cause);
	}
}
