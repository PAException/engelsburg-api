package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.OAuthController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginOAuthRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * RestController for oauth actions.
 */
@RestController
public class OAuthEndpoint {

	@Autowired
	private OAuthController oAuthController;

	/**
	 * Request oauth signup.
	 *
	 * @see OAuthController#request(String, String, HttpServletRequest, HttpServletResponse)
	 */
	@GetMapping("/auth/oauth/{service}")
	public Object request(@PathVariable @NotBlank String service, @RequestParam @NotBlank String schoolToken, HttpServletRequest request, HttpServletResponse response) {
		return this.oAuthController.request(schoolToken, service, request, response).getHttpResponse();
	}

	/**
	 * Handle redirect of oauth server.
	 *
	 * @see OAuthController#redirect(String, HttpServletRequest, HttpServletResponse)
	 */
	@GetMapping("/auth/oauth_redirect/{service}")
	public Object redirect(@PathVariable @NotBlank String service, HttpServletRequest request, HttpServletResponse response) {
		return this.oAuthController.redirect(service, request, response).getHttpResponse();
	}

	/**
	 * Request oauth login.
	 *
	 * @see OAuthController#login(String, LoginOAuthRequestDTO)
	 */
	@GetMapping("/auth/oauth/login/{service}")
	public Object login(@PathVariable @NotBlank String service, @RequestBody @Valid LoginOAuthRequestDTO dto) {
		return this.oAuthController.login(service, dto).getHttpResponse();
	}

}
