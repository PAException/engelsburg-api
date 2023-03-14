/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.GradeModel;
import io.github.paexception.engelsburg.api.database.model.GradeShareModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<GradeModel, Integer> {

	List<GradeModel> findAllByGradeShare(GradeShareModel gradeShare);
}
