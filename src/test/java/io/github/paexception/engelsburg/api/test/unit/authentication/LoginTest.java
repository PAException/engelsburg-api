package io.github.paexception.engelsburg.api.test.unit.authentication;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.controller.internal.RefreshTokenController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_EMAIL;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_PASSWORD;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class LoginTest {

	@InjectMocks
	private AuthenticationController authenticationController;
	@Mock
	private UserRepository userRepository;

	//Dummys
	@Mock
	private RefreshTokenController refreshTokenController;
	@Mock
	private ScopeController scopeController;

	private Result<AuthResponseDTO> request(String email, String password) {
		return this.authenticationController.login(new LoginRequestDTO(email, password));
	}

	private void mockMechanics() {
		when(this.userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(TEST_USER));
	}

	@Test
	public void unknownEmail() {
		this.mockMechanics();

		Result<AuthResponseDTO> dto = this.request("sag@sag.de", TEST_PASSWORD);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.NOT_FOUND);
		assertThat(dto.getExtra()).isEqualTo("user");
	}

	@Test
	public void testWrongPassword() {
		this.mockMechanics();

		Result<AuthResponseDTO> dto = this.request(TEST_EMAIL, "wrong_password");

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.FORBIDDEN);
		assertThat(dto.getExtra()).isEqualTo("wrong_password");
	}

	@Test
	public void login() {
		this.mockMechanics();

		Result<AuthResponseDTO> dto = this.request(TEST_EMAIL, TEST_PASSWORD);

		assertThat(dto.isResultPresent()).isTrue();
		assertThat(dto.getResult().getEmail()).isEqualTo(TEST_EMAIL);
	}

}
