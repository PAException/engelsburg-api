/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.SubstituteMessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public interface SubstituteMessageRepository extends JpaRepository<SubstituteMessageModel, Integer> {

	void deleteByDate(Date date);

	List<SubstituteMessageModel> findAllByDateGreaterThanEqual(Date date);

}
