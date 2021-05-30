package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends PagingAndSortingRepository<ArticleModel, Integer> {

	List<ArticleModel> findAllByDateGreaterThanEqual(long date, Pageable pageable);

}
