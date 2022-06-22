/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FirebaseCloudMessagingImpl implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseCloudMessagingImpl.class);
	private static FirebaseCloudMessagingImpl instance;

	/**
	 * Initiates the firebase app to send notifications.
	 */
	@Bean
	public void init() {
		if (!Environment.PRODUCTION) return;
		if (!FirebaseApp.getApps().isEmpty()) return;
		try {
			FileInputStream serviceAccount = new FileInputStream(Environment.GOOGLE_ACCOUNT_CREDENTIALS);

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();

			FirebaseApp.initializeApp(options);
		} catch (IOException e) {
			this.logError("Couldn't initialize firebase cloud messaging", e, LOGGER);
		}

	}

	/**
	 * Send a notification to one or many topics.
	 *
	 * @param title  of notification
	 * @param body   of notification
	 * @param topics to send notification to
	 */
	public void sendNotificationToTopics(String title, String body, String... topics) {
		if (!Environment.PRODUCTION) return;
		try {
			Message.Builder messageBuilder = Message.builder().setNotification(Notification.builder()
					.setTitle(title).setBody(body).build());

			for (String topic : topics)
				FirebaseMessaging.getInstance().sendAsync(messageBuilder.setTopic(topic
						.replace("Ä", "AE")
						.replace("Ö", "OE")
						.replace("Ü", "UE")
						.replace("ä", "ae")
						.replace("ö", "oe")
						.replace("ü", "ue")
						.toLowerCase()).build());
		} catch (Exception e) {
			this.logError("Couldn't send notification", e, LOGGER);
		}
	}

	/**
	 * Send a multicast notification to many devices.
	 *
	 * @param title  of notification
	 * @param body   of notification
	 * @param tokens devices to send notification to
	 */
	public void sendMulticastNotification(String title, String body, List<String> tokens) {
		if (!Environment.PRODUCTION) return;
		try {
			if (tokens.size() > 0) {
				MulticastMessage multicastMessage = MulticastMessage.builder()
						.addAllTokens(tokens)
						.setNotification(Notification.builder()
								.setTitle(title)
								.setBody(body)
								.build())
						.build();
				FirebaseMessaging.getInstance().sendMulticastAsync(multicastMessage);
			}
		} catch (Exception e) {
			this.logError("Couldn't send notification", e, LOGGER);
		}
	}

	/**
	 * Returns instance.
	 * Creates new if instance is null.
	 *
	 * @return existing or created instance
	 */
	public static FirebaseCloudMessagingImpl getInstance() {
		if (instance == null) instance = new FirebaseCloudMessagingImpl();

		return instance;
	}

}
