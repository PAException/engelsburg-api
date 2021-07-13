package io.github.paexception.engelsburg.api.util;

import io.github.paexception.engelsburg.api.controller.internal.ErrorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LoggingComponent {

	protected final Logger logger;
	@Autowired
	private ErrorController errorController;

	public LoggingComponent(Class<? extends LoggingComponent> loggingClass) {
		this.logger = LoggerFactory.getLogger(loggingClass);
	}

	protected void logError(String msg, Throwable throwable) {
		this.logger.error(msg, throwable);
		this.errorController.handleError(msg, throwable);
	}

}
