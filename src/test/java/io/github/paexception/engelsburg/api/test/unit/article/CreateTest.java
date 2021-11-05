package io.github.paexception.engelsburg.api.test.unit.article;

import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CreateTest {

	private static final ArticleModel TEST_ARTICLE = new ArticleModel(
			-1,
			1,
			0,
			"test_link",
			"test_title",
			"test_content",
			"test_contentHash",
			"test_mediaURL",
			"test_blurHash"
	);
	private static final ArticleModel TEST_ARTICLE2 = new ArticleModel(
			-1,
			1,
			1,
			"test_link",
			"test_title",
			"test_content",
			"test_contentHash",
			"test_mediaURL",
			"test_blurHash"
	);

	@InjectMocks
	private ArticleController articleController;

	@Mock
	private ArticleRepository articleRepository;

	private void mockMechanics() {
		when(this.articleRepository.existsByDate(0)).thenReturn(true);
	}

	@Test
	public void exists() {
		this.mockMechanics();

		this.articleController.createArticle(TEST_ARTICLE.toResponseDTO());

		verify(this.articleRepository, never()).save(any());
	}

	@Test
	public void success() {
		this.mockMechanics();
		this.articleController.createArticle(TEST_ARTICLE2.toResponseDTO());

		verify(this.articleRepository).save(any());
	}


}
