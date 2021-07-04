package io.github.paexception.engelsburg.api.database.repository;


import io.github.paexception.engelsburg.api.database.model.TaskModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {

	void deleteAllByUserId(UUID userId);

	List<TaskModel> findAllByUserId(UUID userId);

	Stream<TaskModel> findAllByUserIdAndCreatedBeforeAndDoneOrderByCreatedDesc(UUID userId, long created, boolean done, Pageable pageable);

	Stream<TaskModel> findAllByUserIdAndCreatedAfterOrderByCreatedAsc(UUID userId, long created, Pageable pageable);

	Stream<TaskModel> findAllByUserIdAndCreatedAfterAndDoneOrderByCreatedAsc(UUID userId, long created, boolean done, Pageable pageable);

	Stream<TaskModel> findAllByUserIdAndCreatedBeforeOrderByCreatedDesc(UUID userId, long created, Pageable pageable);

}
