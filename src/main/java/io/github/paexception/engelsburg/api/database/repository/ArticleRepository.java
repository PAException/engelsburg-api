package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.projections.ArticleIdAndContentHash;
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

	boolean existsByDate(long date);

	Optional<ArticleModel> findByArticleId(int articleId);

	@Query(value = "SELECT * from article", nativeQuery = true)
	List<ArticleIdAndContentHash> findAllIdsAndContentHashes();

	boolean existsByArticleId(int articleId);
}
