package io.github.paexception.engelsburg.api.test.unit.authentication;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.internal.TokenController;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
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
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_TOKEN;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VerifyEmailTest {

	@InjectMocks
	private AuthenticationController authenticationController;
	@Mock
	private UserRepository userRepository;
	@Mock
	private TokenController tokenController;
	@Mock
	private ScopeController scopeController;

	private Result<?> request(boolean testToken) {
		return this.authenticationController.verify(new UserDTO(null, TEST_USER),
				testToken ? "wrong_token" : TEST_TOKEN);
	}

	private void mockMechanics() {
		when(this.userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(TEST_USER));
		when(this.tokenController.checkToken(any(UserModel.class), eq("verify"), eq(TEST_TOKEN))).thenReturn(
				true);
	}

	@Test
	public void wrongToken() {
		this.mockMechanics();

		Result<?> dto = this.request(true);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.FAILED);
		assertThat(dto.getExtra()).isEqualTo("verify");
	}

	@Test
	public void success() {
		this.mockMechanics();
		ArgumentCaptor<String> scope = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<UserModel> user = ArgumentCaptor.forClass(UserModel.class);
		doNothing().when(this.scopeController).addScope(user.capture(), scope.capture());

		Result<?> dto = this.request(false);

		assertThat(dto.isEmpty()).isTrue();

		assertThat(user.getAllValues().stream()
				.filter(u -> !u.getEmail().equals(TEST_EMAIL)).count()).isEqualTo(0);
		assertThat(scope.getAllValues()).isEqualTo(AuthenticationController.VERIFIED_SCOPES);
	}

}
