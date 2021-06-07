package io.github.paexception.engelsburg.api.service.notification;

import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FirebaseCloudMessagingImpl {

	public void sendNotifications(String[] tokens) {
		for (String token : tokens)
			System.out.println("Notification to this device: " + token);
		//TODO
	}

	public void sendAdvancedNotifications(List<NotificationDTO> dtos) {
		dtos.forEach(notificationDTO -> {
			System.out.println("Notification to this devices: " + notificationDTO.getTokens());
		});
		//TODO
	}

}
