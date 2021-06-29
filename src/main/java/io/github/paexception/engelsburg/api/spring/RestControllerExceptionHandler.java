package io.github.paexception.engelsburg.api.spring;

import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles thrown exceptions in RestControllers and represent them in a solid http response
 * Also a way to catch exceptions which are supposed to be thrown or can be ignored
 */
@RestControllerAdvice
public class RestControllerExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerExceptionHandler.class.getSimpleName());

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
		BindingResult result = ex.getBindingResult();

		return Result.of(Error.INVALID_PARAM, result.getFieldError() != null ? result.getFieldError().getObjectName() : null).getHttpResponse();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> exception(Exception exception) {
		LOGGER.error("Caught unhandled error reaching the end of the endpoint pipeline", exception);

		return Result.of(Error.INTERNAL_SERVER_ERROR, "An internal server error occurred!").getHttpResponse();
	}

}
