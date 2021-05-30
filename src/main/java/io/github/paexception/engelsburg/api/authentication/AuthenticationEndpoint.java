package io.github.paexception.engelsburg.api.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.authentication.dto.LoginRequestDTO;
import io.github.paexception.engelsburg.api.authentication.dto.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.authorization.interceptor.AuthScope;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ChangeNotificationSettingsRequestDTO;
import io.github.paexception.engelsburg.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
@RestController
public class AuthenticationEndpoint {

	@Autowired
	private AuthenticationController authenticationController;
	@Autowired
	private NotificationService notificationService;

	@PostMapping("/auth/login")
	public Object login(@RequestBody @Valid LoginRequestDTO dto) {
		return this.authenticationController.login(dto).getHttpResponse();
	}

	@PostMapping("/auth/signup")
	public Object signup(@RequestBody @Valid SignUpRequestDTO dto) {
		return this.authenticationController.signUp(dto).getHttpResponse();
	}

	@PostMapping("/auth/reset_password")
	public Object resetPassword(@RequestParam @NotBlank String email) {
		return this.authenticationController.resetPassword(email).getHttpResponse();
	}

	@AuthScope("notification.settings.write.self")
	@PostMapping("/user/data")
	public Object changeUserInformation(@RequestBody @Valid ChangeNotificationSettingsRequestDTO dto, DecodedJWT jwt) {
		return this.notificationService.changeNotificationSettings(dto, jwt).getHttpResponse();
	}

}
