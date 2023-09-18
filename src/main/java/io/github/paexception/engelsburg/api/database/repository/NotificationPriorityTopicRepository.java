/*
 * Copyright (c) 2023 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.NotificationPriorityTopicModel;
import io.github.paexception.engelsburg.api.database.model.NotificationTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationPriorityTopicRepository extends JpaRepository<NotificationPriorityTopicModel, Integer> {
	List<NotificationPriorityTopicModel> findAllByTopic(String topic);

	List<NotificationPriorityTopicModel> findAllByTopicIn(List<String> topics);

	void deleteAllByNotificationToken(NotificationTokenModel token);
}
