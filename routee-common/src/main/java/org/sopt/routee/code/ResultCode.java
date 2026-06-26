package org.sopt.routee.code;

import org.springframework.http.HttpStatus;

public interface ResultCode {
	HttpStatus getStatus();

	String getMessage();
}
