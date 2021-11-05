package io.github.paexception.engelsburg.api.test.unit.authentication;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.controller.internal.RefreshTokenController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.internal.TokenController;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.service.email.EmailService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_EMAIL;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_HASHED_PASSWORD;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_PASSWORD;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_SALT;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class SignUpTest {

	@InjectMocks
	private AuthenticationController authenticationController;
	@Mock
	private EmailService emailService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ScopeController scopeController;

	//Dummys
	@Mock
	private RefreshTokenController refreshTokenController;
	@Mock
	private TokenController tokenController;

	private Result<AuthResponseDTO> request(boolean testSchoolToken) {
		return this.authenticationController.signUp(
				new SignUpRequestDTO(testSchoolToken ? "wrong_token" : TEST_TOKEN, TEST_EMAIL, TEST_PASSWORD));
	}

	private void mockMechanics() {
		when(this.userRepository.save(any(UserModel.class))).then(returnsFirstArg());
		when(this.emailService.verify(anyString(), isNull())).thenReturn(true);
	}

	@Test
	public void create() {
		this.mockMechanics();

		Result<AuthResponseDTO> dto = this.request(false);

		assertThat(dto.isResultPresent()).isTrue();
		assertThat(dto.getResult()).isInstanceOf(AuthResponseDTO.class);
		assertThat(dto.getResult().getEmail()).isEqualTo(TEST_EMAIL);
	}

	@Test
	public void schoolToken() {
		this.mockMechanics();

		Result<AuthResponseDTO> dto = this.request(true);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.FORBIDDEN);
		assertThat(dto.getExtra()).isEqualTo("school_token");
	}

	@Test
	public void alreadyExists() {
		this.mockMechanics();
		when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(new UserModel()));

		Result<AuthResponseDTO> dto = this.request(false);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.ALREADY_EXISTS);
		assertThat(dto.getExtra()).isEqualTo("user");
	}

	@Test
	public void failOnVerifyEmailError() {
		this.mockMechanics();
		when(this.emailService.verify(anyString(), isNull())).thenReturn(false);

		Result<AuthResponseDTO> dto = this.request(false);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.FAILED);
		assertThat(dto.getExtra()).isEqualTo("signup");
	}

	@Test
	public void scopes() {
		this.mockMechanics();
		ArgumentCaptor<String> scope = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<UserModel> user = ArgumentCaptor.forClass(UserModel.class);
		doNothing().when(this.scopeController).addScope(user.capture(), scope.capture());

		this.request(false);

		assertThat(user.getAllValues().stream()
				.filter(u -> !u.getEmail().equals(TEST_EMAIL)).count()).isEqualTo(0);
		assertThat(scope.getAllValues()).isEqualTo(AuthenticationController.DEFAULT_SCOPES);
	}

	@Test
	public void randomSalt() {
		assertThat(AuthenticationController.randomSalt()).isNotEqualTo(AuthenticationController.randomSalt());
	}

	@Test
	public void hashPassword() {
		assertThat(AuthenticationController.hashPassword(TEST_PASSWORD, TEST_SALT)).isEqualTo(
				AuthenticationController.hashPassword(TEST_PASSWORD, TEST_SALT));
		assertThat(AuthenticationController.hashPassword(TEST_PASSWORD, TEST_SALT)).isEqualTo(TEST_HASHED_PASSWORD);
	}


}
