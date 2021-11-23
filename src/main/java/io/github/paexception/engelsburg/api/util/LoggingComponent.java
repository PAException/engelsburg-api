package io.github.paexception.engelsburg.api.util;

import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import org.slf4j.Logger;

/**
 * Should be used for every component which is logging errors.
 */
public interface LoggingComponent {

	/**
	 * Logs an error which is also send as notification.
	 * {@link NotificationService#sendErrorNotifications(String, Throwable)}
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
		NotificationService.sendErrorNotifications(msg, throwable);
	}

}
