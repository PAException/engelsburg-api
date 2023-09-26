/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.projections.ArticleIdAndContentHashProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends PagingAndSortingRepository<ArticleModel, Integer> {

	List<ArticleModel> findAllByDateGreaterThanEqualOrderByDateAsc(long date, Pageable pageable);

	List<ArticleModel> findAllByDateLessThanEqualOrderByDateDesc(long date, Pageable pageable);

	Optional<ArticleModel> findByArticleId(int articleId);

	@Query(value = "SELECT * from article", nativeQuery = true)
	List<ArticleIdAndContentHashProjection> findAllIdsAndContentHashes();

	boolean existsByArticleId(int articleId);

	boolean existsByContentHash(String hash);
}
