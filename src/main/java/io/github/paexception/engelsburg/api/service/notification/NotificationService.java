package io.github.paexception.engelsburg.api.service.notification;

import io.github.paexception.engelsburg.api.controller.NotificationController;
import io.github.paexception.engelsburg.api.controller.TimetableController;
import io.github.paexception.engelsburg.api.database.model.TimetableModel;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.NotificationDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationService {

	private static final Calendar calendar = Calendar.getInstance();
	@Autowired
	private TimetableController timetableController;
	@Autowired
	private FirebaseCloudMessagingImpl firebaseCloudMessaging;
	@Autowired
	private NotificationController notificationController;

	public void sendSubstituteNotifications(List<SubstituteDTO> dtos) {
		Set<String> tokens = new HashSet<>();
		Set<String> classNames = new HashSet<>();
		Set<String> teachers = new HashSet<>();
		dtos.forEach(dto -> {
			classNames.add(dto.getClassName());
			teachers.add(dto.getTeacher());
			teachers.add(dto.getSubstituteTeacher());
		});

		tokens.addAll(this.notificationController.getNotificationTokensByClassName(classNames));
		tokens.addAll(this.notificationController.getNotificationTokensByTeacher(teachers));
		this.firebaseCloudMessaging.sendNotifications(tokens.toArray(String[]::new));

		this.firebaseCloudMessaging.sendAdvancedNotifications(dtos.stream().map(dto -> {
			calendar.setTime(dto.getDate());
			return Pair.of(this.timetableController.getAllByWeekDayAndLessonAndTeacherOrClassName(
					calendar.get(Calendar.DAY_OF_WEEK),
					dto.getLesson(),
					dto.getTeacher(),
					dto.getClassName()), dto);
		}).map(dtoPair -> new NotificationDTO(
				this.notificationController.getTokensOfUsers(dtoPair.getLeft().stream().map(TimetableModel::getUserId)),
				dtoPair.getRight())).collect(Collectors.toList()
		));
	}

	@EventListener(ApplicationStartedEvent.class)
	public void test() {
		this.sendSubstituteNotifications(List.of(new SubstituteDTO(
				new Date(System.currentTimeMillis()),
				"8c",
				1,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		)));
	}

	public void sendArticleNotifications(ArticleDTO dto) {
		//TODO
	}

}
