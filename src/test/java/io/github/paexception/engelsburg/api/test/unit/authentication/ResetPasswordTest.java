package io.github.paexception.engelsburg.api.test.unit.authentication;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.controller.internal.TokenController;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ResetPasswordRequestDTO;
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
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_NEW_PASSWORD;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_TOKEN;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ResetPasswordTest {

	@InjectMocks
	private AuthenticationController authenticationController;
	@Mock
	private UserRepository userRepository;
	@Mock
	private TokenController tokenController;

	private Result<?> request(boolean testEmail, boolean testToken) {
		return this.authenticationController.resetPassword(new ResetPasswordRequestDTO(
				testEmail ? "wrong_email" : TEST_EMAIL,
				TEST_NEW_PASSWORD,
				testToken ? "wrong_token" : TEST_TOKEN
		));
	}

	private void mockMechanics() {
		when(this.userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(TEST_USER));
		when(this.tokenController.checkToken(any(UserModel.class), eq("reset_password"), eq(TEST_TOKEN))).thenReturn(
				true);
	}

	@Test
	public void unknownEmail() {
		this.mockMechanics();

		Result<?> dto = this.request(true, false);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.NOT_FOUND);
		assertThat(dto.getExtra()).isEqualTo("user");
	}

	@Test
	public void wrongToken() {
		this.mockMechanics();

		Result<?> dto = this.request(false, true);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.FAILED);
		assertThat(dto.getExtra()).isEqualTo("reset_password");
	}


	@Test
	public void success() {
		this.mockMechanics();
		ArgumentCaptor<UserModel> user = ArgumentCaptor.forClass(UserModel.class);
		when(this.userRepository.save(user.capture())).thenReturn(null);

		Result<?> dto = this.request(false, false);

		assertThat(dto.isEmpty()).isTrue();
		assertThat(user.getValue().getPassword()).isNotEqualTo(TEST_HASHED_PASSWORD);
	}

}
