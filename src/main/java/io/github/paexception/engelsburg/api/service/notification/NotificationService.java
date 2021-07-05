package io.github.paexception.engelsburg.api.service.notification;

import io.github.paexception.engelsburg.api.controller.NotificationController;
import io.github.paexception.engelsburg.api.controller.TimetableController;
import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationService {

	private static final Calendar CALENDAR = Calendar.getInstance();
	@Autowired
	private TimetableController timetableController;
	@Autowired
	private FirebaseCloudMessagingImpl firebaseCloudMessaging;
	@Autowired
	private NotificationController notificationController;

	/**
	 * Processes SubstituteDTOs to send as notification.
	 *
	 * @param dtos SubstituteDTOs
	 */
	public void sendSubstituteNotifications(List<SubstituteDTO> dtos) {
		dtos.forEach(dto -> {
			this.firebaseCloudMessaging.sendNotificationToTopics("substitute", dto, this.splitClasses(dto.getClassName()));
			this.firebaseCloudMessaging.sendNotificationToTopics("substitute", dto, "teacher." + dto.getSubstituteTeacher());
		});

		this.firebaseCloudMessaging.sendAdvancedNotifications("substitute", dtos.stream().map(dto -> {
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
	}

	/**
	 * Sends article notifications.
	 *
	 * @param dto ArticleDTO
	 */
	public void sendArticleNotifications(ArticleDTO dto) {
		this.firebaseCloudMessaging.sendNotificationToTopics("article", dto, "article");
	}

	/**
	 * Splits possible className merges like 10ab to 10a, 10b to send notifications to specific topics.
	 *
	 * @param className to split
	 * @return array of classNames
	 */
	private String[] splitClasses(String className) {
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

}
