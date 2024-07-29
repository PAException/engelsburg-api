/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import org.slf4j.Logger;

/**
 * Should be used for every component which is logging errors.
 */
public interface LoggingComponent {

	/**
	 * Logs an error message and the stack trace of the throwable.
	 *
	 * @param msg       the message to log
	 * @param throwable the throwable to log
	 * @param logger    the logger to log to
	 */
	default void logError(String msg, Throwable throwable, Logger logger) {
		logger.error(msg, throwable);
		this.reportToSentry(msg, throwable);
	}

	default void logExpectedError(String msg, Exception exception, Logger logger) {
		logger.error(msg + " because of " + exception.getClass().getCanonicalName() + ": " + exception.getMessage());
		this.reportToSentry(msg, exception);
	}

	/**
	 * Reports an exception to sentry.
	 *
	 * @param msg       the message to log
	 * @param throwable the throwable to log
	 */
	private void reportToSentry(String msg, Throwable throwable) {
		Message message = new Message();
		message.setMessage(msg);

		SentryEvent event = new SentryEvent(throwable);
		event.setMessage(message);

		Sentry.captureEvent(event);
	}
}
