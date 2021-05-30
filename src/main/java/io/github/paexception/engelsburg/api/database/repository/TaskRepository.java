package io.github.paexception.engelsburg.api.database.repository;


import io.github.paexception.engelsburg.api.database.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {

	void deleteAllByUserId(UUID userId);

	List<TaskModel> findAllByUserId(UUID userId);

}
