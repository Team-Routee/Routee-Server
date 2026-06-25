package org.sopt.routee.exception.handler;

import org.sopt.routee.code.ErrorCode;
import org.sopt.routee.exception.BaseException;
import org.sopt.routee.response.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

	@ExceptionHandler(BaseException.class)
	protected ResponseEntity<ApiResponse> handleBaseException(BaseException e) {
		return buildErrorResponse(e.getCode());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<ApiResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
		return buildErrorResponse(ErrorCode.MISSING_REQUEST_PARAMETER);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	protected ResponseEntity<ApiResponse> handleMissingRequestHeader(MissingRequestHeaderException e) {
		return buildErrorResponse(ErrorCode.MISSING_REQUEST_HEADER);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
		return buildErrorResponse(ErrorCode.INVALID_REQUEST_BODY);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
		return buildErrorResponse(ErrorCode.INVALID_PARAMETER_TYPE);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ApiResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());
		return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, message);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	protected ResponseEntity<ApiResponse> handleNoResourceFound(NoResourceFoundException e) {
		return buildErrorResponse(ErrorCode.NOT_FOUND);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ApiResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
		return buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	protected ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
		return buildErrorResponse(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ApiResponse> handleException(Exception e) {
		return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}
