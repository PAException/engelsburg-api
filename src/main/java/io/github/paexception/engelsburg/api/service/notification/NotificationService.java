/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.notification;

import io.github.paexception.engelsburg.api.controller.reserved.NotificationSettingsController;
import io.github.paexception.engelsburg.api.controller.reserved.TimetableController;
import io.github.paexception.engelsburg.api.database.model.NotificationDeviceModel;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.ErrorNotificationDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteNotificationDTO;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.Pair;
import io.github.paexception.engelsburg.api.util.l10n.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationService implements LoggingComponent {

	private static final Calendar CALENDAR = Calendar.getInstance();
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
	private static final FirebaseCloudMessagingImpl FCM = FirebaseCloudMessagingImpl.getInstance();
	private final TimetableController timetableController;
	private final NotificationSettingsController notificationSettingsController;

	public NotificationService(
			TimetableController timetableController,
			NotificationSettingsController notificationSettingsController) {
		this.timetableController = timetableController;
		this.notificationSettingsController = notificationSettingsController;
	}

	/**
	 * Sends error notifications.
	 *
	 * @param msg       error message
	 * @param throwable exception
	 */
	public static void sendErrorNotifications(String msg, Throwable throwable) {
		ErrorNotificationDTO dto = new ErrorNotificationDTO();
		dto.setMessage(msg);
		dto.setErrorMessage(throwable.getMessage());

		String[] stacktrace = new String[throwable.getStackTrace().length];
		for (int i = 0; i < throwable.getStackTrace().length; i++)
			stacktrace[i] = throwable.getStackTrace()[i].toString();
		dto.setStacktrace(stacktrace);

		FCM.sendNotificationToTopics("[ERROR]", msg, "error");
	}

	/**
	 * Splits possible className merges like 10ab to 10a, 10b to send notifications to specific topics.
	 *
	 * @param className to split
	 * @return array of classNames
	 */
	private static String[] splitClasses(String className) {
		if (className.length() <= 2 || (Character.isDigit(className.charAt(1)) && className.length() == 3)) {
			return new String[]{"class." + className};
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
						strings.add("class." + curr + c);
						curr = new StringBuilder();
					}
				} else {
					if (Character.isLowerCase(c)) {
						write = false;
						strings.add("class." + curr + c);
					} else {
						curr = new StringBuilder();
						adv = true;
						curr.append(c);
					}
				}
			}

			return strings.toArray(String[]::new);
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
		return (created ? Localization.string(langCode, "changed") + ": " : "")
				+ substitute.getType() + " " + substitute.getLesson();
	}

	/**
	 * Processes SubstituteDTOs to send as notification.
	 *
	 * @param dtos    SubstituteDTOs
	 * @param created if given substitutes have been created
	 */
	public void sendSubstituteNotifications(List<SubstituteDTO> dtos, boolean created) {
		final String langCode = "de_DE";
		LOGGER.debug("Starting to send " + dtos.size() + " substitute notification" + (dtos.size() != 1 ? "s" : ""));

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

		//Send general substitute notifications if they have been created
		if (created) {
			FCM.sendNotificationToTopics(
					Localization.string(langCode, "newSubstitute").placeholder("count", notificationDTOs.size()).get(),
					null,
					"substitute"
			);
		}


		//Send substitute notifications to topics (classes and teacher)
		for (SubstituteNotificationDTO dto : notificationDTOs) {
			FCM.sendNotificationToTopics(//Classes
					getSubstituteTitle(dto, created, langCode),
					getSubstituteText(dto, langCode),
					splitClasses(dto.getClassName())
			);

			FCM.sendNotificationToTopics(//Teacher
					getSubstituteTitle(dto, created, langCode),
					getSubstituteText(dto, langCode),
					"teacher." + dto.getSubstituteTeacher()
			);
		}

		//Send substitute notifications via timetable
		List<Pair<Set<NotificationDeviceModel>, SubstituteNotificationDTO>> list = dtos.stream()
				.map(dto -> {
					CALENDAR.setTime(dto.getDate());
					return Pair.of(this.timetableController.getAllByWeekDayAndLessonAndTeacherOrClassName(
							CALENDAR.get(Calendar.DAY_OF_WEEK) - 2, //MON starts at 2
							dto.getLesson(),
							dto.getTeacher(),
							dto.getClassName()
					), dto);
				})
				.filter(pair -> !pair.getLeft().isEmpty())
				.map(pair -> Pair.of(
						this.notificationSettingsController.getTimetableNotificationDeviceOfUsers(
								pair.getLeft().stream().map(
										timetable -> timetable.getSubject().getSemester().getUser())),
						SubstituteNotificationDTO.fromSubstituteDTO(pair.getRight(), null)
				))
				.collect(Collectors.toList());

		for (Pair<Set<NotificationDeviceModel>, SubstituteNotificationDTO> pair : list) {
			FCM.sendMulticastNotification(
					getSubstituteTitle(pair.getRight(), created, langCode),
					getSubstituteText(pair.getRight(), langCode),
					pair.getLeft().stream().map(NotificationDeviceModel::getToken).collect(Collectors.toList())
			);
		}
		LOGGER.debug("Sent substitute notifications");
	}

	/**
	 * Sends article notifications.
	 *
	 * @param dto ArticleDTO
	 */
	public void sendArticleNotifications(ArticleDTO dto) {
		LOGGER.info("Sending article notifications");
		FCM.sendNotificationToTopics(Localization.string("de_DE", "newArticle").get(), dto.getTitle(), "article");
	}

}
