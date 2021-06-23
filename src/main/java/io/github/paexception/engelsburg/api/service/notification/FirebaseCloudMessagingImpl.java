package io.github.paexception.engelsburg.api.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class FirebaseCloudMessagingImpl {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Bean
	public void init() throws IOException {
		FileInputStream serviceAccount = new FileInputStream(System.getenv("GOOGLE_ACCOUNT_CREDENTIALS"));

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		FirebaseApp.initializeApp(options);
	}

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

	private void sendMessage(Object message, String... tokens) {

	}

}
