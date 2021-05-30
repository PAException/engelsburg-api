package io.github.paexception.engelsburg.api;

import io.github.paexception.engelsburg.api.controller.ArticleController;
import io.github.paexception.engelsburg.api.database.model.ArticleModel;
import io.github.paexception.engelsburg.api.database.repository.ArticleRepository;
import io.github.paexception.engelsburg.api.endpoint.ArticleEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {ArticleEndpoint.class, ArticleController.class})
public class ArticleEndpointTest {

	@Autowired private MockMvc mockMvc;

	@MockBean private ArticleRepository articleRepository;

	@Test
	public void testArticles() throws Exception {
		ArticleModel articleModel = new ArticleModel(0, 0, "https://localhost", "Hello World", "Hey", "https://localhost/media");
		Mockito.when(this.articleRepository.findAllByDateGreaterThanEqual(anyLong(), any()))
				.thenReturn(Collections.singletonList(articleModel));

		MvcResult result = this.mockMvc.perform(get("/article").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().json("{\"articles\":[{\"date\":0,\"link\":\"https://localhost\",\"title\":\"Hello World\",\"content\":\"Hey\",\"mediaUrl\":\"https://localhost/media\"}]}"))
				.andReturn();

		Mockito.verify(articleRepository).findAllByDateGreaterThanEqual(anyLong(), any());
	}

	@Test
	public void testNotFound() throws Exception {
		Mockito.when(this.articleRepository.findAllByDateGreaterThanEqual(anyLong(), any()))
				.thenReturn(Collections.emptyList());

		this.mockMvc.perform(get("/article").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().json("{\"status\":404,\"messageKey\":\"NOT_FOUND\",\"extra\":\"article\"}"))
				.andReturn();

		Mockito.verify(this.articleRepository).findAllByDateGreaterThanEqual(anyLong(), any());
	}

}