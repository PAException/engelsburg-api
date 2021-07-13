package io.github.paexception.engelsburg.api.controller.internal;

import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ErrorController {

	@Autowired
	private NotificationService notificationService;

	public void handleError(String msg, Throwable throwable) {
		this.notificationService.sendErrorNotifications(msg, throwable);
	}

}
