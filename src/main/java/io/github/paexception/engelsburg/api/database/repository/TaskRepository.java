package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.TaskModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {

	void deleteAllByUser(UserModel user);

	List<TaskModel> findAllByUser(UserModel user);

	Stream<TaskModel> findAllByUserAndCreatedBeforeAndDoneOrderByCreatedDesc(UserModel user, long created, boolean done,
			Pageable pageable);

	Stream<TaskModel> findAllByUserAndCreatedAfterOrderByCreatedAsc(UserModel user, long created, Pageable pageable);

	Stream<TaskModel> findAllByUserAndCreatedAfterAndDoneOrderByCreatedAsc(UserModel user, long created, boolean done,
			Pageable pageable);

	Stream<TaskModel> findAllByUserAndCreatedBeforeOrderByCreatedDesc(UserModel user, long created, Pageable pageable);

}
