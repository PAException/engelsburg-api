/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import org.slf4j.Logger;

/**
 * Should be used for every component which is logging errors.
 */
public interface LoggingComponent {

	/**
	 * Logs an error which is also send as notification.
	 *
	 * <p>Should be called on every error log instead of {@link Logger#error(String, Throwable)},
	 * otherwise notification won't be sent.</p>
	 *
	 * @param msg       to log (simple error explanation)
	 * @param throwable actual error
	 * @param logger    to log error to
	 */
	default void logError(String msg, Throwable throwable, Logger logger) {
		logger.error(msg, throwable);
	}
}
