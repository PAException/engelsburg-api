package io.github.paexception.engelsburg.api.test.unit.authentication;

import io.github.paexception.engelsburg.api.controller.AuthenticationController;
import io.github.paexception.engelsburg.api.controller.internal.TokenController;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.service.email.EmailService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_EMAIL;
import static io.github.paexception.engelsburg.api.test.TestUtils.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class RequestResetPasswordTest {

	@InjectMocks
	private AuthenticationController authenticationController;
	@Mock
	private EmailService emailService;
	@Mock
	private UserRepository userRepository;

	//Dummy
	@Mock
	private TokenController tokenController;

	private Result<?> request(boolean testEmail) {
		return this.authenticationController.requestResetPassword(testEmail ? "wrong_email" : TEST_EMAIL);
	}

	private void mockMechanics() {
		when(this.userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(TEST_USER));
		when(this.emailService.resetPassword(anyString(), isNull())).thenReturn(true);
	}


	@Test
	public void unknownEmail() {
		this.mockMechanics();

		Result<?> dto = this.request(true);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.NOT_FOUND);
		assertThat(dto.getExtra()).isEqualTo("user");
	}

	@Test
	public void failOnVerifyEmailError() {
		this.mockMechanics();
		when(this.emailService.resetPassword(anyString(), isNull())).thenReturn(false);

		Result<?> dto = this.request(false);

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.FAILED);
		assertThat(dto.getExtra()).isEqualTo("request_reset_password");
	}

	@Test
	public void success() {
		this.mockMechanics();

		Result<?> dto = this.request(false);

		assertThat(dto.isEmpty()).isTrue();
	}

}
