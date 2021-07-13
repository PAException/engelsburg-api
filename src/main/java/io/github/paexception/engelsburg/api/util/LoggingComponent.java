package io.github.paexception.engelsburg.api.util;

import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LoggingComponent {

	protected final Logger logger;
	@Autowired
	private NotificationService notificationService;

	public LoggingComponent(Class<? extends LoggingComponent> loggingClass) {
		this.logger = LoggerFactory.getLogger(loggingClass);
	}

	protected void logError(String msg, Throwable throwable) {
		this.logger.error(msg, throwable);
		this.notificationService.sendErrorNotifications(msg, throwable);
	}

}
