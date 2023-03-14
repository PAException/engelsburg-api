/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.TaskModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.stream.Stream;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {

	Stream<TaskModel> findAllBySemesterAndCreatedBeforeAndDoneOrderByCreatedDesc(SemesterModel semester, long created,
			boolean done,
			Pageable pageable);

	Stream<TaskModel> findAllBySemesterAndCreatedAfterOrderByCreatedAsc(SemesterModel semester, long created,
			Pageable pageable);

	Stream<TaskModel> findAllBySemesterAndCreatedAfterAndDoneOrderByCreatedAsc(SemesterModel semester, long created,
			boolean done,
			Pageable pageable);

	Stream<TaskModel> findAllBySemesterAndCreatedBeforeOrderByCreatedDesc(SemesterModel semester, long created,
			Pageable pageable);
}
