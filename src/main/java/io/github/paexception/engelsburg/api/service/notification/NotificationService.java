/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import io.github.paexception.engelsburg.api.controller.reserved.NotificationSettingsController;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteNotificationDTO;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
	 * @return list of classNames
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
	 * @return formatted text
	 */
	private static String getSubstituteText(@NotNull SubstituteNotificationDTO substitute) {
		String lesson = "[" + substitute.getLesson() + "]";
		String type = " " + substitute.getType();
		String className = "";
		if (substitute.getClassName() != null) className = " - " + substitute.getClassName();

		String subject = "";
		if (substitute.getSubject() != null) subject = " - " + substitute.getSubject();

		String teachers = "";
		if (substitute.getTeacher() != null && substitute.getSubstituteTeacher() != null) {
			teachers = " (" + substitute.getSubstituteTeacher() + " statt " + substitute.getTeacher() + ")";
		} else if (substitute.getSubstituteTeacher() != null) {
			teachers = " (" + substitute.getSubstituteTeacher() + ")";
		}

		String room = "";
		if (substitute.getRoom() != null) room = " in " + substitute.getRoom();

		String text = "";
		if (substitute.getText() != null) text = " - " + substitute.getText();

		String substituteOf = "";
		if (substitute.getSubstituteOf() != null) substituteOf = " - " + substitute.getSubstituteOf();

		return lesson + type + className + subject + teachers + room + text + substituteOf;
	}

	/**
	 * Return a title of substitute notification.
	 *
	 * @param substitute substitute to format
	 * @param created    if substitute was updated or created
	 * @return title
	 */
	private static String getSubstituteTitle(SubstituteNotificationDTO substitute, boolean created) {
		String actuality = created ? "Neue" : "Geänderte";
		String relationalDay;
		if (DateUtils.isSameDay(substitute.getDate(), Date.from(Instant.now()))) {
			relationalDay = "heute";
		} else if (DateUtils.isSameDay(substitute.getDate(), Date.from(Instant.now().plus(Duration.ofDays(1))))) {
			relationalDay = "morgen";
		} else {
			relationalDay = "den " + DateTimeFormatter.ofPattern("dd.MM.").format(substitute.getDate().toLocalDate());
		}

		return actuality + " Vertretung für " + relationalDay;
	}

	/**
	 * Combines same substitutes.
	 * E.g. if 5th and 6th lesson are equal.
	 *
	 * @param dtos raw substitute data
	 * @return the combined dtos ready to send
	 */
	private static List<SubstituteNotificationDTO> combineDTOs(List<SubstituteDTO> dtos) {
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

		return notificationDTOs;
	}

	/**
	 * Processes SubstituteDTOs to send as notification.
	 *
	 * @param dtos    SubstituteDTOs
	 * @param created if given substitutes have been created
	 */
	@Async
	public void sendSubstituteNotifications(List<SubstituteDTO> dtos, boolean created) {
		LOGGER.debug(
				"[NOTIFICATION] Starting to send " + dtos.size() + " substitute notification" + (dtos.size() != 1 ? "s" : ""));

		//Remove same substitutes (e.g. 5th and 6th lesson)
		List<SubstituteNotificationDTO> notificationDTOs = combineDTOs(dtos);
		Map<String, String> data = Map.of("link", "/substitutes");

		try {
			//Send general substitute notifications if they have been created
			if (created) {
				List<String> tokens = notificationSettingsController.getTokensOf("substitute");

				List<String> failed = FCM.sendMulticastNotification(
						notificationDTOs.size() + " Vertretung(en) geändert oder veröffentlicht!",
						null,
						tokens,
						data
				);

				//Delete all tokens that failed
				this.notificationSettingsController.deleteInvalidTokens(failed);
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
					List<String> failed = FCM.sendMulticastNotification(
							getSubstituteTitle(dto, created),
							getSubstituteText(dto),
							new ArrayList<>(tokens),
							data
					);

					//Delete all tokens that failed
					this.notificationSettingsController.deleteInvalidTokens(failed);
				}
			}
		} catch (FirebaseMessagingException e) {
			this.logError("[NOTIFICATION] Couldn't send notifications", e, LOGGER);
		}
		LOGGER.info("[NOTIFICATION] Sent " + dtos.size() + " substitute notification" + (dtos.size() != 1 ? "s" : ""));
	}

	/**
	 * Sends article notifications.
	 *
	 * @param dto ArticleDTO
	 */
	public void sendArticleNotifications(ArticleDTO dto) {
		LOGGER.info("[NOTIFICATION] Sending article notifications (articleId: " + dto.getArticleId() + ")");
		List<String> tokens = notificationSettingsController.getTokensOf("article");

		try {
			List<String> failed = FCM.sendMulticastNotification(
					"Neuer Artikel veröffentlicht!",
					dto.getTitle(),
					tokens,
					Map.of("link", "/article")
			);

			//Delete all tokens that failed
			this.notificationSettingsController.deleteInvalidTokens(failed);
		} catch (FirebaseMessagingException e) {
			this.logError("[NOTIFICATION] Couldn't send notifications", e, LOGGER);
		}
	}

}
