/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.notification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import io.github.paexception.engelsburg.api.controller.reserved.NotificationSettingsController;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteNotificationDTO;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.l10n.Localization;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@AllArgsConstructor
public class NotificationService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
	private static final FirebaseCloudMessagingImpl FCM = FirebaseCloudMessagingImpl.getInstance();

	private final NotificationSettingsController notificationSettingsController;

	/**
	 * Splits possible className merges like 10ab to 10a, 10b to send notifications to specific topics.
	 *
	 * @param className to split
	 * @return array of classNames
	 */
	private static List<String> splitClasses(String className) {
		if (className.length() <= 2 || (Character.isDigit(className.charAt(1)) && className.length() == 3)) {
			return List.of(className);
		} else { //5ab or 5ab6ab or E2Q2Q4
			List<String> strings = new ArrayList<>();
			StringBuilder curr = new StringBuilder();
			char c;
			boolean write = false, adv = false;
			for (int i = 0; i < className.length(); i++) {
				c = className.charAt(i);
				if (Character.isDigit(c)) {
					if (!adv || write) {
						if (!write) {
							write = true;
							curr = new StringBuilder();
						}
						curr.append(c);
					} else {
						strings.add(curr.toString() + c);
						curr = new StringBuilder();
					}
				} else {
					if (Character.isLowerCase(c)) {
						write = false;
						strings.add(curr.toString() + c);
					} else {
						curr = new StringBuilder();
						adv = true;
						curr.append(c);
					}
				}
			}

			return strings;
		}
	}

	/**
	 * Return a formatted text to display substitutes.
	 *
	 * @param substitute substitute to format
	 * @param langCode   language code
	 * @return formatted text
	 */
	private static String getSubstituteText(@NotNull SubstituteNotificationDTO substitute, @NotNull String langCode) {
		return (substitute.getClassName() == null ? "" : substitute.getClassName()) +
				(substitute.getClassName() == null ? "" : " – ") +
				(substitute.getSubject() == null ? "" : substitute.getSubject()) + " (" +
				(substitute.getSubstituteTeacher() == null || substitute.getSubstituteTeacher().equals("+")
						? ""
						: substitute.getSubstituteTeacher()) +
				(substitute.getSubstituteTeacher() != null &&
						!substitute.getSubstituteTeacher().equals("+") &&
						substitute.getTeacher() == null
						? ")"
						: "") +
				(substitute.getSubstituteTeacher() != null &&
						!substitute.getSubstituteTeacher().equals("+") &&
						substitute.getTeacher() != null &&
						!substitute.getSubstituteTeacher().equals(substitute.getTeacher())
						? " " + Localization.string(langCode, "insteadOf") + " "
						: "") +
				(substitute.getTeacher() == null || substitute.getTeacher().equals(substitute.getSubstituteTeacher())
						? ""
						: substitute.getTeacher()) +
				(substitute.getTeacher() != null ? ")" : "") +
				(substitute.getRoom() == null ? "" : " in " + substitute.getRoom()) +
				(substitute.getText() == null || substitute.getText().isEmpty()
						? ""
						: " – " + substitute.getText()) +
				(substitute.getSubstituteOf() == null ? "" : " – " + substitute.getSubstituteOf());
	}

	/**
	 * Return a title of substitute notification.
	 *
	 * @param substitute substitute to format
	 * @param created    if substitute was updated or created
	 * @param langCode   language abbreviation
	 * @return title
	 */
	private static String getSubstituteTitle(SubstituteNotificationDTO substitute, boolean created, String langCode) {
		return (!created ? Localization.string(langCode, "changed") + ": " : "")
				+ substitute.getLesson() + " " + substitute.getType();
	}

	/**
	 * Processes SubstituteDTOs to send as notification.
	 *
	 * @param dtos    SubstituteDTOs
	 * @param created if given substitutes have been created
	 */
	@Async
	public void sendSubstituteNotifications(List<SubstituteDTO> dtos, boolean created) {
		final String langCode = "de_DE";
		LOGGER.debug(
				"[Notification] Starting to send " + dtos.size() + " substitute notification" + (dtos.size() != 1 ? "s" : ""));

		//Remove same substitutes (e.g. 5th and 6th lesson)
		List<SubstituteNotificationDTO> notificationDTOs = new ArrayList<>();
		for (int i = 0; i < dtos.size(); i++) {
			List<Integer> same = new ArrayList<>();
			SubstituteDTO dto = dtos.get(i);
			if (dto == null) continue;
			int low = 0, high = 0;
			var sub = dtos.get(i);
			for (var ii = 0; ii < dtos.size(); ii++) {
				if (ii != i) {
					var compare = dtos.get(ii);
					if (
							sub.getDate() == compare.getDate() &&
									Objects.equals(sub.getClassName(), compare.getClassName()) &&
									Objects.equals(sub.getTeacher(), compare.getTeacher()) &&
									Objects.equals(sub.getSubstituteTeacher(), compare.getSubstituteTeacher()) &&
									Objects.equals(sub.getRoom(), compare.getRoom()) &&
									Objects.equals(sub.getSubject(), compare.getSubject()) &&
									Objects.equals(sub.getType(), compare.getType()) &&
									Objects.equals(sub.getSubstituteOf(), compare.getSubstituteOf())
					) {
						same.add(ii);

						if (sub.getLesson() > compare.getLesson()) {
							high = sub.getLesson();
							low = compare.getLesson();
						} else if (sub.getLesson() < compare.getLesson()) {
							high = compare.getLesson();
							low = sub.getLesson();
						}
					}
				}
			}
			if (!same.isEmpty()) {
				notificationDTOs.add(SubstituteNotificationDTO.fromSubstituteDTO(
						dtos.get(same.get(same.size() - 1)),
						low + " - " + high
				));
				for (Integer element : same) {
					dtos.remove(element.intValue());
				}
			} else {
				notificationDTOs.add(SubstituteNotificationDTO.fromSubstituteDTO(dto, null));
			}
		}
		Map<String, String> data = Map.of("link", "/substitutes");

		//Send general substitute notifications if they have been created
		if (created) {
			FCM.sendNotificationToTopics(
					Localization.string(langCode, "newSubstitute").placeholder("count", notificationDTOs.size()).get(),
					null,
					data,
					"substitute"
			);
		}


		//Send substitute notifications to topics (classes and teacher)
		for (SubstituteNotificationDTO dto : notificationDTOs) {
			Set<String> tokens = new HashSet<>();

			//Classes
			if (dto.getClassName() != null) {
				for (String className : splitClasses(dto.getClassName().toUpperCase()))
					tokens.addAll(this.notificationSettingsController.getTokensOf("substitute.class." + className));
			}

			//Teacher
			if (dto.getTeacher() != null) {
				String teacher = "substitute.teacher." + dto.getTeacher().toUpperCase();
				tokens.addAll(this.notificationSettingsController.getTokensOf(teacher));
			}

			//Timetable
			tokens.addAll(this.notificationSettingsController.getTimetableTokens(dto));

			//Send notification to all tokens
			if (!tokens.isEmpty()) {
				try {
					List<String> tokenList = new ArrayList<>(tokens);
					BatchResponse responses = FCM.sendMulticastNotification(
							getSubstituteTitle(dto, created, langCode),
							getSubstituteText(dto, langCode),
							tokenList,
							data
					);

					//Get all tokens that failed
					List<String> invalidTokens = new ArrayList<>();
					List<SendResponse> responsesResponses = responses.getResponses();
					for (int i = 0; i < responsesResponses.size(); i++) {
						SendResponse response = responsesResponses.get(i);
						if (response.isSuccessful()) continue;

						if (response.getException().getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED))
							invalidTokens.add(tokenList.get(i));
					}

					//Delete all tokens that failed
					this.notificationSettingsController.deleteInvalidTokens(invalidTokens);
				} catch (FirebaseMessagingException e) {
					this.logError("[Notification] Couldn't send notifications", e, LOGGER);
				}
			}
		}
		LOGGER.info("[Notification] Sent " + dtos.size() + " substitute notification" + (dtos.size() != 1 ? "s" : ""));
	}

	/**
	 * Sends article notifications.
	 *
	 * @param dto ArticleDTO
	 */
	public void sendArticleNotifications(ArticleDTO dto) {
		LOGGER.info("[Notification] Sending article notifications (articleId: " + dto.getArticleId() + ")");
		FCM.sendNotificationToTopics(
				Localization.string("de_DE", "newArticle").get(),
				dto.getTitle(),
				Map.of("link", "/article"),
				"article"
		);
	}

}
