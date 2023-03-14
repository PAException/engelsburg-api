/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.integration;

import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.internal.UserController;
import io.github.paexception.engelsburg.api.controller.shared.ArticleController;
import io.github.paexception.engelsburg.api.database.repository.ArticleSaveRepository;
import io.github.paexception.engelsburg.api.database.repository.user.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.AuthenticationEndpoint;
import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetArticlesResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.integration.endpoint.AuthenticationEndpointIntegrationTest;
import io.github.paexception.engelsburg.api.endpoint.shared.ArticleEndpoint;
import io.github.paexception.engelsburg.api.endpoint.util.EndpointTest;
import io.github.paexception.engelsburg.api.util.Error;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static io.github.paexception.engelsburg.api.endpoint.util.TestUtils.assertThatIsError;
import static io.github.paexception.engelsburg.api.endpoint.util.TestUtils.jsonRequest;
import static io.github.paexception.engelsburg.api.endpoint.util.TestUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;

@EndpointTest
public class InterceptorIntegrationTest {

	@Autowired
	private AuthenticationEndpoint authenticationEndpoint;
	@Autowired
	private ArticleEndpoint articleEndpoint;

	@Autowired
	private ArticleController articleController;
	@Autowired
	private UserController userController;
	@Autowired
	private ScopeController scopeController;

	@Autowired
	private ArticleSaveRepository articleSaveRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mvc;

	@Test
	public void testHashInterceptor() throws Exception {
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.get("/article/0"));
		MvcResult result = this.mvc.perform(builder).andReturn();

		String hash = result.getResponse().getHeader("Hash");
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/article/0")
				.header("Hash", hash));
		result = this.mvc.perform(builder).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(304);
	}

	@Test
	public void testPaging() throws Exception {
		for (int i = 0; i < 45; i++)
			this.articleController.createOrUpdateArticle(new ArticleDTO(
					i + 1000,
					System.currentTimeMillis(),
					"some_link",
					"article_" + i,
					"custom content: " + i * i,
					"some_hash",
					"mediaUrl",
					"blur"
			));


		//No paging specified
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.get("/article"));
		MvcResult result = this.mvc.perform(builder).andReturn();

		GetArticlesResponseDTO dto = parse(result, GetArticlesResponseDTO.class);
		assertThat(dto.getArticles().size()).isEqualTo(20);


		//Page specified
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/article")
				.param("page", "1"));
		result = this.mvc.perform(builder).andReturn();

		GetArticlesResponseDTO dto2 = parse(result, GetArticlesResponseDTO.class);
		assertThat(dto2.getArticles().size()).isEqualTo(20);
		assertThat(dto2.getArticles()).isNotEqualTo(dto.getArticles());


		//Size and page specified
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/article")
				.param("size", "1")
				.param("page", "1"));
		result = this.mvc.perform(builder).andReturn();

		dto2 = parse(result, GetArticlesResponseDTO.class);
		assertThat(dto2.getArticles().size()).isEqualTo(1);
		assertThat(dto2.getArticles()).isNotEqualTo(dto.getArticles());
		assertThat(dto2.getArticles().get(0)).isEqualTo(dto.getArticles().get(1));


		//Size specified
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/article")
				.param("size", "1"));
		result = this.mvc.perform(builder).andReturn();

		dto = parse(result, GetArticlesResponseDTO.class);
		assertThat(dto.getArticles().size()).isEqualTo(1);


		//Size to big
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/article")
				.param("size", "200"));
		result = this.mvc.perform(builder).andReturn();

		dto = parse(result, GetArticlesResponseDTO.class);
		assertThat(dto.getArticles().size()).isEqualTo(20);


		//Page to small
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/article")
				.param("page", "-1"));
		result = this.mvc.perform(builder).andReturn();

		dto2 = parse(result, GetArticlesResponseDTO.class);
		assertThat(dto2.getArticles().size()).isEqualTo(20);
		assertThat(dto.getArticles()).isEqualTo(dto2.getArticles());
	}

	@Test
	public void testRateLimit() throws Exception {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
					.get("/article")
					.with(request -> {
						request.setRemoteAddr("192.168.178.2");
						return request;
					}));
			this.mvc.perform(builder);
		}
		while (System.currentTimeMillis() < start + 1000 * 12) ;

		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.get("/article")
				.with(request -> {
					request.setRemoteAddr("192.168.178.2");
					return request;
				}));
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(200);


		//Fail
		for (int i = 0; i < 9; i++) {
			builder = jsonRequest(MockMvcRequestBuilders
					.get("/article")
					.with(request -> {
						request.setRemoteAddr("192.168.178.2");
						return request;
					}));
			this.mvc.perform(builder);
		}

		builder = jsonRequest(MockMvcRequestBuilders
				.get("/article")
				.with(request -> {
					request.setRemoteAddr("192.168.178.2");
					return request;
				}));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.TOO_MANY_REQUESTS);
	}

	@Test
	public void testAuthentication() throws Exception {
		//No token
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.patch("/article/save/118"));
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.UNAUTHORIZED.copyWithExtra("token"));


		//Expired
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjk4MWY2Yi0wMmJjLTQ0MjAtYWM1Mi1mMzljNWNjMGEzNzMiLCJpc3MiOiJlbmdlbHNidXJnLWFwaSIsInNjb3BlcyI6ImluZm8uY2xhc3Nlcy5yZWFkLmFsbC0tdGVhY2hlci5yZWFkLmFsbC0tLW5vdGlmaWNhdGlvbi5zZXR0aW5ncy5yZWFkLnNlbGYtd3JpdGUuc2VsZi0tLXN1YnN0aXR1dGUubWVzc2FnZS5yZWFkLmN1cnJlbnQtLXJlYWQuY3VycmVudC0tdXNlci5kYXRhLmRlbGV0ZS5zZWxmLXJlYWQuc2VsZiIsImV4cCI6MTY0NTA1NTE1NiwiaWF0IjoxNjQ1MDU1MTU1fQ.YLuGdveBgCr3yVb5U9q6i0HkIaB94UIAXBiFvUVWCe4IcVYD268n2G_kdg7kNQaqUPHrvlAirN5CGKARujxLlg";
		builder = jsonRequest(MockMvcRequestBuilders
				.patch("/article/save/118")
				.header("Authorization", token));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.EXPIRED.copyWithExtra("token"));


		//Invalid format
		token = "eyJ0eXAiOiJKV1QiLCJhbGciJ9.eyJzdWIiOiIxMjk4MWY2Yi0wMmJjLTQ0MjAtYWM1Mi1mMzljNWNjMGEzNzMiLCJpc3MiOiJlbmdlbHNidXJnLWFwaSIsInNjb3BlcyI6ImluZm8uY2xhc3Nlcy5yZWFkLmFsbC0tdGVhY2hlci5yZWFkLmFsbC0tLW5vdGlmaWNhdGlvbi5zZXR0aW5ncy5yZWFkLnNlbGYtd3JpdGUuc2VsZi0tLXN1YnN0aXR1dGUubWVzc2FnZS5yZWFkLmN1cnJlbnQtLXJlYWQuY3VycmVudC0tdXNlci5kYXRhLmRlbGV0ZS5zZWxmLXJlYWQuc2VsZiIsImV4cCI6MTY0NTA1NTE1NiwiaWF0IjoxNjQ1MDU1MTU1fQ.YLuGdveBgCr3yVb5U9q6i0HkIaB94UIAXBiFvUVWCe4IcVYD268n2G_kdg7kNQaqUPHrvlAirN5CGKARujxLlg";
		builder = jsonRequest(MockMvcRequestBuilders
				.patch("/article/save/118")
				.header("Authorization", token));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID.copyWithExtra("token"));


		//False alg
		token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjk4MWY2Yi0wMmJjLTQ0MjAtYWM1Mi1mMzljNWNjMGEzNzMiLCJpc3MiOiJlbmdlbHNidXJnLWFwaSIsInNjb3BlcyI6ImluZm8uY2xhc3Nlcy5yZWFkLmFsbC0tdGVhY2hlci5yZWFkLmFsbC0tLW5vdGlmaWNhdGlvbi5zZXR0aW5ncy5yZWFkLnNlbGYtd3JpdGUuc2VsZi0tLXN1YnN0aXR1dGUubWVzc2FnZS5yZWFkLmN1cnJlbnQtLXJlYWQuY3VycmVudC0tdXNlci5kYXRhLmRlbGV0ZS5zZWxmLXJlYWQuc2VsZiIsImV4cCI6MTg0NDY3NDQwNzM3MDk1NTIwMDAsImlhdCI6MTY0NTA1NTE1NX0.d9yIb18gntosk5KGScnPru9C-OEpudDoipgkYtBLB6Y";
		builder = jsonRequest(MockMvcRequestBuilders
				.patch("/article/save/118")
				.header("Authorization", token));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID.copyWithExtra("token"));


		//No scope
		builder = jsonRequest(MockMvcRequestBuilders
				.patch("/article/save/118")
				.header("Authorization", AuthenticationEndpointIntegrationTest.signUp(this.mvc).getToken()));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.FORBIDDEN.copyWithExtra("auth"));


		//Success
		this.articleController.createOrUpdateArticle(new ArticleDTO(
				118,
				System.currentTimeMillis(),
				"some_link",
				"article_",
				"custom content: ",
				"some_hash",
				"mediaUrl",
				"blur"
		));
		this.scopeController.addScope(this.userController.getAll().get(0), "article.save.write.self");
		builder = jsonRequest(MockMvcRequestBuilders
				.patch("/article/save/118")
				.header("Authorization", AuthenticationEndpointIntegrationTest.login(this.mvc).getToken()));
		result = this.mvc.perform(builder).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@AfterAll
	public void cleanUp() {
		this.userRepository.deleteAll();
		this.articleSaveRepository.deleteAll();
	}
}
