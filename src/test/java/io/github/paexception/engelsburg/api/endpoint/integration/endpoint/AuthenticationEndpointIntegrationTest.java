/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.integration.endpoint;

import io.github.paexception.engelsburg.api.database.model.TokenModel;
import io.github.paexception.engelsburg.api.database.repository.TokenRepository;
import io.github.paexception.engelsburg.api.database.repository.user.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.AuthenticationEndpoint;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ResetPasswordRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.util.EndpointTest;
import io.github.paexception.engelsburg.api.util.Error;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import static io.github.paexception.engelsburg.api.endpoint.util.TestUtils.assertThatEmpty;
import static io.github.paexception.engelsburg.api.endpoint.util.TestUtils.assertThatIsError;
import static io.github.paexception.engelsburg.api.endpoint.util.TestUtils.jsonRequest;
import static io.github.paexception.engelsburg.api.endpoint.util.TestUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;

@EndpointTest
public class AuthenticationEndpointIntegrationTest {

	@Autowired
	private AuthenticationEndpoint authenticationEndpoint;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private MockMvc mvc;

	public static AuthResponseDTO login(MockMvc mvc) throws Exception {
		return login(mvc, "password");
	}

	public static AuthResponseDTO login(MockMvc mvc, String password) throws Exception {
		LoginRequestDTO dto = new LoginRequestDTO("test@gmail.com", password);
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/login"), dto);

		MvcResult result = mvc.perform(builder).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);

		return parse(result, AuthResponseDTO.class);
	}

	public static AuthResponseDTO signUp(MockMvc mvc) throws Exception {
		SignUpRequestDTO dto = new SignUpRequestDTO("test@gmail.com", "password");
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/signup"), dto);
		MvcResult result = mvc.perform(builder).andReturn();
		AuthResponseDTO authResponse = parse(result, AuthResponseDTO.class);

		assertThat(authResponse.getEmail()).isEqualTo(dto.getEmail());
		assertThat(authResponse.isVerified()).isFalse();

		return authResponse;
	}

	private AuthResponseDTO login() throws Exception {
		return login(this.mvc);
	}

	private AuthResponseDTO login(String password) throws Exception {
		return login(this.mvc, password);
	}

	private AuthResponseDTO signUp() throws Exception {
		return signUp(this.mvc);
	}

	@Test
	@Commit
	@Order(1)
	void testSignUp() throws Exception {
		//No email
		SignUpRequestDTO dto = new SignUpRequestDTO(null, "password");
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/signup"), dto);
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("email"));


		//Invalid email
		dto = new SignUpRequestDTO("test.gmail.com", "password");
		builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/signup"), dto);
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("email"));


		//No password
		dto = new SignUpRequestDTO("test@gmail.com", null);
		builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/signup"), dto);
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("password"));


		//Actual signup
		dto = new SignUpRequestDTO("test@gmail.com", "password");
		builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/signup"), dto);
		result = this.mvc.perform(builder).andReturn();

		//Check that verify token is created
		List<TokenModel> tokens = this.tokenRepository.findAllByUser(this.userRepository.findAll().get(0));
		assertThat(tokens.stream().filter(token -> token.getType().equals("verify")).count() == 1).isTrue();


		//User already exists
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.ALREADY_EXISTS.copyWithExtra("user"));
	}

	@Test
	@Order(2)
	public void testLogin() throws Exception {
		//No email
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
						.post("/auth/login"),
				new LoginRequestDTO(null, "password"));
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("email"));


		//Invalid email
		builder = jsonRequest(MockMvcRequestBuilders
						.post("/auth/login"),
				new LoginRequestDTO("test.gmail.com", "password"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("email"));


		//No password
		builder = jsonRequest(MockMvcRequestBuilders
						.post("/auth/login"),
				new LoginRequestDTO("test@gmail.com", null));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("password"));


		//User not found
		builder = jsonRequest(MockMvcRequestBuilders
						.post("/auth/login"),
				new LoginRequestDTO("test2@gmail.com", "password"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.NOT_FOUND.copyWithExtra("user"));


		//Wrong password
		builder = jsonRequest(MockMvcRequestBuilders
						.post("/auth/login"),
				new LoginRequestDTO("test@gmail.com", "wrong_password"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.FORBIDDEN.copyWithExtra("wrong_password"));


		//Actual login
		this.login();
	}

	@Test
	@Order(3)
	public void testAuth() throws Exception {
		//No refreshToken
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.get("/auth/refresh"));
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("refreshToken"));


		//Invalid refreshToken
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/auth/refresh")
				.param("refreshToken", "false_token"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.FAILED.copyWithExtra("refreshToken"));


		//Actual refresh
		builder = jsonRequest(MockMvcRequestBuilders
				.get("/auth/refresh")
				.param("refreshToken", this.login().getRefreshToken()));
		result = this.mvc.perform(builder).andReturn();
		AuthResponseDTO authResponse = parse(result, AuthResponseDTO.class);

		assertThat(authResponse.getEmail()).isEqualTo("test@gmail.com");
	}

	@Test
	@Order(4)
	public void testVerify() throws Exception {
		//No token
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.patch("/auth/verify"));
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(404);


		//Wrong token
		builder = jsonRequest(MockMvcRequestBuilders
				.patch("/auth/verify/falseToken"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.FAILED.copyWithExtra("verify"));


		//Right token
		String token = this.tokenRepository.findAllByUser(this.userRepository.findAll().get(0)).stream()
				.filter(tokenModel -> tokenModel.getType().equals("verify"))
				.findFirst().orElseThrow().getToken().split(",")[0];
		builder = jsonRequest(MockMvcRequestBuilders
				.patch("/auth/verify/" + token));
		result = this.mvc.perform(builder).andReturn();

		assertThatEmpty(result);
	}

	@Test
	@Commit
	@Order(5)
	public void testRequestResetPassword() throws Exception {
		//Without email
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/request_reset_password"));
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("email"));


		//User does not exists
		builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/request_reset_password")
				.param("email", "test2@gmail.com"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.NOT_FOUND.copyWithExtra("user"));


		//With email
		builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/request_reset_password")
				.param("email", "test@gmail.com"));
		result = this.mvc.perform(builder).andReturn();

		assertThatEmpty(result);


		//With user logged in
		String accessToken = this.login().getToken();
		builder = jsonRequest(MockMvcRequestBuilders
				.post("/auth/request_reset_password")
				.param("email", "test2@gmail.com")
				.header("Authorization", accessToken));
		result = this.mvc.perform(builder).andReturn();

		assertThatEmpty(result);

		//Check that no email send to test2@gmail.com
		List<TokenModel> tokens = this.tokenRepository.findAllByUser(this.userRepository.findAll().get(0));
		tokens.removeIf(token -> token.getToken().contains(",test@gmail.com") || token.getType().equals("verify"));
		assertThat(tokens.size() == 0).isTrue();
	}

	@Test
	@Order(6)
	public void testResetPassword() throws Exception {
		//Test no token
		RequestBuilder builder = jsonRequest(MockMvcRequestBuilders
						.patch("/auth/reset_password"),
				new ResetPasswordRequestDTO("newPassword", null));
		MvcResult result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("token"));


		//Test no password
		builder = jsonRequest(MockMvcRequestBuilders
						.patch("/auth/reset_password"),
				new ResetPasswordRequestDTO(null, "someToken"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.INVALID_PARAM.copyWithExtra("password"));


		//Test false token
		builder = jsonRequest(MockMvcRequestBuilders
						.patch("/auth/reset_password"),
				new ResetPasswordRequestDTO("newPassword", "false_token"));
		result = this.mvc.perform(builder).andReturn();

		assertThatIsError(result, Error.FAILED.copyWithExtra("reset_password"));


		//Actual password reset
		String token = this.tokenRepository.findAllByUser(this.userRepository.findAll().get(0)).stream()
				.filter(tokenModel -> tokenModel.getType().equals("reset_password"))
				.findFirst().orElseThrow().getToken().split(",")[0];
		builder = jsonRequest(MockMvcRequestBuilders
						.patch("/auth/reset_password"),
				new ResetPasswordRequestDTO("newPassword", token));
		result = this.mvc.perform(builder).andReturn();

		assertThatEmpty(result);

		this.login("newPassword");
	}

	@AfterAll
	public void cleanUp() {
		this.userRepository.deleteAll();
	}
}