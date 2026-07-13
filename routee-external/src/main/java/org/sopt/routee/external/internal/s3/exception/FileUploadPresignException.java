package org.sopt.routee.external.internal.s3.exception;

import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.internal.s3.code.ErrorCode;

public final class FileUploadPresignException extends BaseException {

	public FileUploadPresignException(Throwable cause) {
		super(ErrorCode.FILE_UPLOAD_PRESIGN_FAILED, cause);
	}
}
