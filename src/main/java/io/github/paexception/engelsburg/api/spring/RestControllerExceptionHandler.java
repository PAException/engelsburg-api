package io.github.paexception.engelsburg.api.spring;

import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles thrown exceptions in RestControllers and represent them in a solid http response
 * Also a way to catch exceptions which are supposed to be thrown or can be ignored
 */
@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerExceptionHandler.class.getSimpleName());

	/**
	 * Handle any exception that was thrown and not be caught or processed
	 *
	 * @param exception that was thrown
	 * @return INTERNAL_SERVER_ERROR
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> exception(Exception exception) {
		LOGGER.error("Caught unhandled error reaching the end of the endpoint pipeline", exception);

		return Result.of(Error.INTERNAL_SERVER_ERROR, "An internal server error occurred!").getHttpResponse();
	}

	/**
	 * Override method to handle exception already processed by {@link ResponseEntityExceptionHandler}
	 * where {@link ExceptionHandler} would throw an error, because exception is already handled
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		if (ex instanceof MethodArgumentNotValidException) {
			BindingResult result = ((MethodArgumentNotValidException) ex).getBindingResult();

			StringBuilder stringBuilder = new StringBuilder();
			result.getFieldErrors().forEach(field -> stringBuilder.append(field.getField()).append(", "));
			stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

			return Result.of(Error.INVALID_PARAM, stringBuilder.toString()).getHttpResponse();
		} else if (ex instanceof HttpMessageNotReadableException) {
			return Result.of(Error.INVALID_PARAM, "possible missing body or content type").getHttpResponse();
		} else if (ex instanceof MissingServletRequestParameterException) {
			return Result.of(Error.INVALID_PARAM, "missing request param: "
					+ ((MissingServletRequestParameterException) ex).getParameterName()).getHttpResponse();
		} else return Result.of(Error.fromHttpStatus(status)).getHttpResponse();
	}

}
