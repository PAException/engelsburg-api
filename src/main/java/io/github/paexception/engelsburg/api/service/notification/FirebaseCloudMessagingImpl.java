package io.github.paexception.engelsburg.api.service.notification;

import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FirebaseCloudMessagingImpl {

	public void sendNotifications(String[] tokens) {
		for (String token : tokens)
			System.out.println("Substitute notification to this device: " + token);
		//TODO
	}

	public void sendAdvancedNotifications(List<NotificationDTO> dtos) {
		dtos.forEach(notificationDTO -> {
			System.out.println("Advanced substitute notification to this devices: " + notificationDTO.getTokens());
		});
		//TODO
	}

	public void sendArticleNotifications(String[] tokens, ArticleDTO dto) {
		for (String token : tokens)
			System.out.println("Advanced article notification to this device: " + token);
		//TODO
	}

}
