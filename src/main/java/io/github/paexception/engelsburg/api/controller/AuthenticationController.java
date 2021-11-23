package io.github.paexception.engelsburg.api.controller;

import com.auth0.jwt.JWTCreator;
import io.github.paexception.engelsburg.api.controller.internal.RefreshTokenController;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.internal.TokenController;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ResetPasswordRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.AuthResponseDTO;
import io.github.paexception.engelsburg.api.service.email.EmailService;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Hash;
import io.github.paexception.engelsburg.api.util.JwtUtil;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for authentication.
 */
@Component
public class AuthenticationController implements UserDataHandler {

	/**
	 * List of default scopes to grant to user by creating an account.
	 */
	public static final List<String> DEFAULT_SCOPES = List.of(
			"substitute.message.read.current",
			"substitute.read.current",

			"info.teacher.read.all",
			"info.classes.read.all",

			"notification.settings.write.self",
			"notification.settings.read.self",

			"user.data.read.self",
			"user.data.delete.self"
	);
	/**
	 * List of scopes to grant to user by verifying the email address.
	 */
	public static final List<String> VERIFIED_SCOPES = List.of(
			"article.save.write.self",
			"article.save.delete.self",
			"article.save.read.self",

			"grade.write.self",
			"grade.read.self",
			"grade.delete.self",

			"task.delete.self",
			"task.write.self",
			"task.read.self",

			"timetable.write.self",
			"timetable.read.self",
			"timetable.delete.self"
	);

	private final EmailService emailService;

	private final ScopeController scopeController;
	private final TokenController tokenController;
	private final RefreshTokenController refreshTokenController;

	private final UserRepository userRepository;

	public AuthenticationController(EmailService emailService,
			ScopeController scopeController,
			TokenController tokenController,
			RefreshTokenController refreshTokenController,
			UserRepository userRepository) {
		this.emailService = emailService;
		this.scopeController = scopeController;
		this.tokenController = tokenController;
		this.refreshTokenController = refreshTokenController;
		this.userRepository = userRepository;
	}

	/**
	 * Hash a password.
	 *
	 * @param password password
	 * @param salt     and salt to hash
	 * @return valid hash
	 */
	public static byte[] hashPassword(String password, String salt) {
		String parsed = ((password == null ? RandomStringUtils.randomAlphanumeric(16) : password) + salt);

		return Hash.sha256(parsed);
	}

	/**
	 * Create a new random salt.
	 *
	 * @return salt
	 */
	public static String randomSalt() {
		return RandomStringUtils.randomAlphanumeric(16);
	}

	/**
	 * Sign up with credentials and schoolToken.
	 *
	 * @param dto email, password and schoolToken
	 * @return empty response or error
	 */
	public Result<AuthResponseDTO> signUp(SignUpRequestDTO dto) {
		if (!dto.getSchoolToken().equals(Environment.SCHOOL_TOKEN))
			return Result.of(Error.FORBIDDEN, "school_token");

		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isPresent()) return Result.of(Error.ALREADY_EXISTS, "user");

		String salt = randomSalt();
		byte[] hashedPassword = hashPassword(dto.getPassword(), salt);

		UUID userId = UUID.randomUUID();
		UserModel user = this.userRepository.save(
				new UserModel(-1, userId, dto.getEmail(), hashedPassword, salt, false));
		DEFAULT_SCOPES.forEach(scope -> this.scopeController.addScope(user, scope)); //Add default scopes
		if (this.emailService.verify(
				dto.getEmail(), this.tokenController.createRandomToken(user, "verify"))) {
			return Result.of(this.createAuthResponse(user));
		} else {
			this.userRepository.deleteByUserId(userId);
			return Result.of(Error.FAILED, "signup");
		}
	}

	/**
	 * Login with credentials.
	 *
	 * @param dto email and password
	 * @return valid jwt token
	 */
	public Result<AuthResponseDTO> login(LoginRequestDTO dto) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, "user");

		UserModel user = optionalUser.get();
		if (!Arrays.equals(user.getPassword(), hashPassword(dto.getPassword(), user.getSalt())))
			return Result.of(Error.FORBIDDEN, "wrong_password");

		return Result.of(this.createAuthResponse(user));
	}

	/**
	 * Authenticate and request JWT via refresh token.
	 *
	 * @param refreshToken to get JWT
	 * @return JWT
	 */
	public Result<AuthResponseDTO> auth(String refreshToken) {
		UserModel user = this.refreshTokenController.verifyRefreshToken(refreshToken);
		if (user == null) return Result.of(Error.FAILED, "refresh_token");

		return Result.of(this.createAuthResponse(user));
	}

	/**
	 * Request to reset a password of existing account.
	 * An email will be sent with a token to reset the password
	 *
	 * @param email of account to reset
	 * @return empty response
	 */
	public Result<?> requestResetPassword(String email) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, "user");

		if (this.emailService.resetPassword(
				email,
				this.tokenController.createRandomTemporaryToken(optionalUser.get(), "reset_password",
						System.currentTimeMillis() + 1000 * 60 * 30)
		)) return Result.empty();
		else return Result.of(Error.FAILED, "request_reset_password");
	}

	/**
	 * Set the new password with password reset token.
	 *
	 * @param dto with email, new password and password reset token
	 * @return empty result or error
	 */
	public Result<?> resetPassword(ResetPasswordRequestDTO dto) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, "user");

		UserModel user = optionalUser.get();
		if (this.tokenController.checkToken(user, "reset_password", dto.getToken())) {
			user.setPassword(hashPassword(dto.getPassword(), user.getSalt()));
			this.userRepository.save(user);
			this.tokenController.deleteToken(user, "reset_password", dto.getToken());
			return Result.empty();
		} else return Result.of(Error.FAILED, "reset_password");
	}

	/**
	 * Verifies a user.
	 *
	 * @param userDTO with userId
	 * @param token   to verify
	 * @return empty response or error
	 */
	public Result<?> verify(UserDTO userDTO, String token) {
		UserModel user = userDTO.user;
		if (this.tokenController.checkToken(user, "verify", token)) {
			user.setVerified(true);
			this.userRepository.save(user);
			this.tokenController.deleteToken(user, "verify", token);
			VERIFIED_SCOPES.forEach(scope -> this.scopeController.addScope(user, scope));
			return Result.empty();
		} else return Result.of(Error.FAILED, "verify");
	}

	/**
	 * Update default scopes of all current users.
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void updateDefaultScopes() {
		this.userRepository.findAll().forEach(user -> this.scopeController.updateScopes(user, DEFAULT_SCOPES));
		this.userRepository.findAllByVerified(true).forEach(
				user -> this.scopeController.updateScopes(user, VERIFIED_SCOPES));
	}

	/**
	 * Create a valid jwt token.
	 *
	 * @param user uuid as subject of jwt and verified information
	 * @return jwt token
	 */
	public AuthResponseDTO createAuthResponse(UserModel user) {
		JWTCreator.Builder builder = JwtUtil.getInstance()
				.createBuilder(user.getUserId().toString(), 5, Calendar.MINUTE);
		String[] scopes = this.scopeController.getScopes(user);

		return new AuthResponseDTO(
				JwtUtil.getInstance().sign(builder.withClaim("scopes", ScopeController.mergeScopes(scopes))),
				this.refreshTokenController.create(user), user.getEmail(), user.isVerified());
	}

	/**
	 * Simply get a user by its userId.
	 *
	 * @param userId to get user.
	 * @return user
	 */
	public UserModel getUser(UUID userId) {
		return this.userRepository.findByUserId(userId);
	}

	@Override
	public void deleteUserData(UserModel user) {
		this.userRepository.deleteByUserId(user.getUserId());
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.userRepository.findByUserId(user.getUserId()));
	}
}
