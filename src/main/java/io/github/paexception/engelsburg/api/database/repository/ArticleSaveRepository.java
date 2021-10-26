package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ArticleSaveModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleSaveRepository extends PagingAndSortingRepository<ArticleSaveModel, Integer> {

	int countAllByArticleId(int articleId);

	boolean existsByArticleIdAndUserId(int articleId, UUID userId);

	void deleteByArticleIdAndUserId(int articleId, UUID userId);

	void deleteAllByUserId(UUID userId);

	List<ArticleSaveModel> findAllByUserId(UUID userId);

}
