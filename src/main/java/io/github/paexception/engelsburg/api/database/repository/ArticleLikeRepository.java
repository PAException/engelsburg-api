package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ArticleLikeModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleLikeRepository extends PagingAndSortingRepository<ArticleLikeModel, Integer> {

	int countAllByArticleId(int articleId);

	boolean existsByArticleIdAndUserId(int articleId, UUID userId);

	void deleteByArticleIdAndUserId(int articleId, UUID userId);

	void deleteAllByUserId(UUID userId);

	List<ArticleLikeModel> findAllByUserId(UUID userId);

}
