/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.GradeShareModel;
import io.github.paexception.engelsburg.api.database.model.SubjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeShareRepository extends JpaRepository<GradeShareModel, Integer> {

	List<GradeShareModel> findAllBySubject(SubjectModel subject);
}
