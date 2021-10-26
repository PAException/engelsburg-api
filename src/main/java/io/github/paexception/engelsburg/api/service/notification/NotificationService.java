package io.github.paexception.engelsburg.api.service.notification;

import io.github.paexception.engelsburg.api.controller.reserved.NotificationController;
import io.github.paexception.engelsburg.api.controller.reserved.TimetableController;
import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.ErrorNotificationDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationService implements LoggingComponent {

	private static final Calendar CALENDAR = Calendar.getInstance();
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
	private static final FirebaseCloudMessagingImpl FCM = FirebaseCloudMessagingImpl.getInstance();
	@Autowired
	private TimetableController timetableController;
	@Autowired
	private NotificationController notificationController;

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

		FCM.sendNotificationToTopics("error", dto, "error");
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
	 * Processes SubstituteDTOs to send as notification.
	 *
	 * @param dtos SubstituteDTOs
	 */
	public void sendSubstituteNotifications(List<SubstituteDTO> dtos) {
		LOGGER.debug("Starting to send substitute notifications");
		dtos.forEach(dto -> {
			FCM.sendNotificationToTopics("substitute", dto, splitClasses(dto.getClassName()));
			FCM.sendNotificationToTopics("substitute", dto, "teacher." + dto.getSubstituteTeacher());
		});

		FCM.sendAdvancedNotifications("substitute", dtos.stream().map(dto -> {
					CALENDAR.setTime(dto.getDate());
					return Pair.of(this.timetableController.getAllByWeekDayAndLessonAndTeacherOrClassName(
							CALENDAR.get(Calendar.DAY_OF_WEEK) - 2, //MON starts at 2
							dto.getLesson(),
							dto.getTeacher(),
							dto.getClassName()
					), dto);
				}).filter(dtoPair -> !dtoPair.getLeft().isEmpty())
						.map(dtoPair -> new NotificationDTO(
								this.notificationController.getTimetableNotificationTokensOfUsers(dtoPair.getLeft().stream().map(TimetableModel::getUserId)),
								dtoPair.getRight()
						)).collect(Collectors.toList())
		);
		LOGGER.debug("Sent substitute notifications");
	}

	/**
	 * Sends article notifications.
	 *
	 * @param dto ArticleDTO
	 */
	public void sendArticleNotifications(ArticleDTO dto) {
		LOGGER.info("Sending article notifications");
		FCM.sendNotificationToTopics("article", dto, "article");
	}

}
