/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ArticleSaveModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleSaveRepository extends PagingAndSortingRepository<ArticleSaveModel, Integer> {

	boolean existsByArticleIdAndUser(int articleId, UserModel user);

	void deleteByArticleIdAndUser(int articleId, UserModel user);

	List<ArticleSaveModel> findAllByUser(UserModel user);
}
