package io.github.paexception.engelsburg.api.util;

import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import org.slf4j.Logger;

public interface LoggingComponent {

	default void logError(String msg, Throwable throwable, Logger logger) {
		logger.error(msg, throwable);
		NotificationService.sendErrorNotifications(msg, throwable);
	}

}
