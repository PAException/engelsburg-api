package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface ArticleRepository extends PagingAndSortingRepository<ArticleModel, Integer> {

	List<ArticleModel> findAllByDateGreaterThanEqual(long date, Pageable pageable);

}
