package io.github.paexception.engelsburg.api.test.unit.authentication;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.controller.internal.RefreshTokenController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_EMAIL;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_TOKEN;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthTest {

	@InjectMocks
	private AuthenticationController authenticationController;
	@Mock
	private RefreshTokenController refreshTokenController;

	//Dummys
	@Mock
	private ScopeController scopeController;

	private void mockMechanics() {
		when(this.refreshTokenController.verifyRefreshToken(TEST_TOKEN)).thenReturn(TEST_USER);
	}

	@Test
	public void wrongToken() {
		this.mockMechanics();

		Result<AuthResponseDTO> dto = this.authenticationController.auth("wrong_token");

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.FAILED);
		assertThat(dto.getExtra()).isEqualTo("refresh_token");
	}

	@Test
	public void auth() {
		this.mockMechanics();

		Result<AuthResponseDTO> dto = this.authenticationController.auth(TEST_TOKEN);

		assertThat(dto.isResultPresent()).isTrue();
		assertThat(dto.getResult().getEmail()).isEqualTo(TEST_EMAIL);
	}

}
