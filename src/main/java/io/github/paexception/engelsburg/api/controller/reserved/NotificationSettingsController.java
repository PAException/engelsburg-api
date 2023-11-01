/*
 * Copyright (c) 2023 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.NotificationPriorityTopicModel;
import io.github.paexception.engelsburg.api.database.model.NotificationTokenModel;
import io.github.paexception.engelsburg.api.database.repository.NotificationPriorityTopicRepository;
import io.github.paexception.engelsburg.api.database.repository.NotificationTokenRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteNotificationDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static io.github.paexception.engelsburg.api.util.Constants.NotificationSettings.NAME_KEY;

/**
 * Handles notification settings of clients with the FCM tokens.
 * <p>
 * - substitute.teacher.[TEACHER]> => specific announcement of new substitutes FOR TEACHER
 * - substitute.class.[CLASSNAME] => specific announcement of new substitutes FOR CLASS
 * - substitute.timetable.[DAY].[LESSON].[TEACHER] => specific announcement of new substitutes by DAY, LESSON, TEACHER
 * - substitute.timetable.[DAY].[LESSON].[CLASSNAME] => specific announcement of new substitutes by DAY, LESSON, CLASS
 * </p>
 */
@Component
@AllArgsConstructor
public class NotificationSettingsController {

	private final NotificationTokenRepository tokenRepository;
	private final NotificationPriorityTopicRepository priorityTopicRepository;

	@Transactional
	public Result<?> updateNotificationSettings(UpdateNotificationSettingsRequestDTO dto) {
		this.deleteNotificationSettings(dto.getToken());
		this.tokenRepository.flush();

		NotificationTokenModel token = new NotificationTokenModel(dto.getToken());
		this.tokenRepository.save(token);

		//Save all priorityTopics of dto to database, only if each topic is not blank
		List<NotificationPriorityTopicModel> topics = new ArrayList<>();
		for (String priorityTopic : dto.getPriorityTopics())
			if (!priorityTopic.isBlank()) topics.add(new NotificationPriorityTopicModel(priorityTopic, token));

		this.priorityTopicRepository.saveAll(topics);

		return Result.empty();
	}

	@Transactional
	public Result<?> deleteNotificationSettings(String token) {
		//Get token, if not found return error
		Optional<NotificationTokenModel> optionalToken = this.tokenRepository.findByToken(token);
		if (optionalToken.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//If found cascade delete
		this.tokenRepository.delete(optionalToken.get());

		return Result.empty();
	}

	public List<String> tokensOfPriorityTopic(List<NotificationPriorityTopicModel> priorityTopics) {
		Set<String> tokens = new HashSet<>();

		//Map all topics to get the corresponding tokens
		for (NotificationPriorityTopicModel priorityTopic : priorityTopics)
			tokens.add(priorityTopic.getNotificationToken().getToken());

		return new ArrayList<>(tokens);
	}

	@Transactional
	public List<String> getTokensOf(String identifier) {
		//Get all topics by given identifier
		List<NotificationPriorityTopicModel> priorityTopics = this.priorityTopicRepository.findAllByTopic(identifier);

		return this.tokensOfPriorityTopic(priorityTopics);
	}

	@Transactional
	public List<String> getTimetableTokens(SubstituteNotificationDTO dto) {
		//Extract variables
		String prefix = "substitute.timetable";
		int day = dto.getDate().toLocalDate().getDayOfWeek().getValue();
		String teacher = dto.getTeacher();
		String className = dto.getClassName();

		//Split lessons, cycle through all if it is combined
		String[] splitLesson = dto.getLesson().split("-");
		int lower = Integer.parseInt(splitLesson[0].trim());
		int upper = splitLesson.length == 1 ? lower : Integer.parseInt(splitLesson[1].trim());

		//Add all possible combinations
		List<String> combinations = new ArrayList<>();
		for (int i = lower; i <= upper; i++) {
			if (teacher != null && !teacher.isEmpty()) {
				combinations.add(prefix + "." + day + "." + i + "." + teacher);
			}

			if (className != null && !className.isEmpty()) {
				combinations.add(prefix + "." + day + "." + i + "." + className);
			}
		}

		//Return all tokens that matched the combinations
		return this.tokensOfPriorityTopic(this.priorityTopicRepository.findAllByTopicIn(combinations));
	}

	@Transactional
	public void deleteInvalidTokens(List<String> invalidTokens) {
		//Delete all invalid tokens
		this.tokenRepository.deleteAllByTokenIn(invalidTokens);
	}
}
