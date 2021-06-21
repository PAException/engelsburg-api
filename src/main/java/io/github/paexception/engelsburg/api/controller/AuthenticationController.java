package io.github.paexception.engelsburg.api.controller;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.EngelsburgAPI;
import io.github.paexception.engelsburg.api.controller.internal.ScopeController;
import io.github.paexception.engelsburg.api.controller.internal.TokenController;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.LoginRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.ResetPasswordRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.LoginResponseDTO;
import io.github.paexception.engelsburg.api.service.email.EmailService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import static io.github.paexception.engelsburg.api.util.Constants.Authentication.NAME_KEY;

/**
 * Controller for authentication
 */
@Component
public class AuthenticationController implements UserDataHandler {

	private static final Random RANDOM = new SecureRandom();
	private static final List<String> DEFAULT_SCOPES = List.of(
			"grade.write.self",
			"grade.read.self",
			"grade.delete.self",

			"info.teacher.read.all",
			"info.classes.read.all",

			"notification.settings.write.self",
			"notification.settings.read.self",

			"substitute.message.read.current",
			"substitute.read.current",

			"task.delete.self",
			"task.write.self",
			"task.read.self",

			"timetable.write.self",
			"timetable.read.self",
			"timetable.delete.self",

			"user.data.read.self",
			"user.data.delete.self"
	);
	private static MessageDigest md;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ScopeController scopeController;
	@Autowired
	private EmailService emailService;
	@Autowired
	private TokenController tokenController;

	/**
	 * Login with credentials
	 *
	 * @param dto email and password
	 * @return valid jwt token
	 */
	public Result<?> login(LoginRequestDTO dto) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		UserModel user = optionalUser.get();
		if (!user.isVerified()) return Result.of(Error.NOT_VERIFIED, NAME_KEY);
		if (!user.getPassword().equals(this.hashPassword(dto.getPassword(), user.getSalt())))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		return Result.of(new LoginResponseDTO(this.createJWT(user)));
	}

	/**
	 * Sign up with credentials and schoolToken
	 *
	 * @param dto email, password and schoolToken
	 * @return empty response or error
	 */
	public Result<?> signUp(SignUpRequestDTO dto) {
		if (!dto.getSchoolToken().equals(System.getenv("SCHOOL_TOKEN")))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isPresent()) return Result.of(Error.ALREADY_EXISTS, NAME_KEY);
		byte[] rawSalt = new byte[16];
		RANDOM.nextBytes(rawSalt);
		String salt = new String(rawSalt, StandardCharsets.UTF_8);
		String hashedPassword = this.hashPassword(dto.getPassword(), salt);

		UUID userId = UUID.randomUUID();
		this.userRepository.save(new UserModel(-1, userId, dto.getEmail(), hashedPassword, salt, false));
		DEFAULT_SCOPES.forEach(scope -> this.scopeController.addScope(userId, scope));//Add default scopes
		if (this.emailService.verify(
				dto.getEmail(),
				this.tokenController.createRandomToken(userId, "verify")
		)) return Result.empty();
		else return Result.of(Error.FAILED, NAME_KEY);
	}

	/**
	 * Request to reset a password of existing account.
	 * An email will be send with a token to reset the password
	 *
	 * @param email of account to reset
	 * @return empty response
	 */
	public Result<?> requestResetPassword(String email) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		if (this.emailService.resetPassword(
				email,
				this.tokenController.createRandomTemporaryToken(optionalUser.get().getUserId(), "reset_password", System.currentTimeMillis() + 1000 * 60 * 30)
		)) return Result.empty();
		else return Result.of(Error.FAILED, NAME_KEY);
	}

	/**
	 * Set the new password with password reset token
	 *
	 * @param dto with email, new password and password reset token
	 * @return empty result or error
	 */
	public Result<?> resetPassword(ResetPasswordRequestDTO dto) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		UserModel user = optionalUser.get();
		if (this.tokenController.checkToken(user.getUserId(), "reset_password", dto.getToken())) {
			user.setPassword(this.hashPassword(dto.getPassword(), user.getSalt()));
			this.userRepository.save(user);
			this.tokenController.deleteToken(user.getUserId(), "reset_password", dto.getToken());
			return Result.empty();
		} else return Result.of(Error.FAILED, NAME_KEY);
	}

	/**
	 * Verifies a user
	 *
	 * @param jwt   with userId
	 * @param token to verify
	 * @return empty response or error
	 */
	public Result<?> verify(DecodedJWT jwt, String token) {
		UUID userId = UUID.fromString(jwt.getSubject());
		UserModel user = this.userRepository.findByUserId(userId);
		if (this.tokenController.checkToken(userId, "verify", token)) {
			user.setVerified(true);
			this.userRepository.save(user);
			this.tokenController.deleteToken(userId, "verify", token);
			return Result.empty();
		} else return Result.of(Error.FAILED, NAME_KEY);
	}

	/**
	 * Update default scopes of all current users
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void updateDefaultScopes() {
		this.userRepository.findAll().forEach(user -> this.scopeController.updateScopes(user, DEFAULT_SCOPES));
	}

	/**
	 * Create a valid jwt token
	 *
	 * @param user with userId as subject of jwt
	 * @return jwt token
	 */
	private String createJWT(UserModel user) {
		JWTCreator.Builder builder = EngelsburgAPI.getJwtUtil()
				.createBuilder(user.getUserId().toString(), 30, Calendar.MINUTE);
		String[] scopes = this.scopeController.getScopes(user.getUserId());

		return EngelsburgAPI.getJwtUtil().sign(builder.withArrayClaim("scopes", scopes));
	}

	/**
	 * Hash a password
	 *
	 * @param password password
	 * @param salt     and salt to hash
	 * @return valid hash
	 */
	private String hashPassword(String password, String salt) {
		try {
			if (md == null) md = MessageDigest.getInstance("SHA-256");

			return new String(md.digest((password + salt).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException ignored) {//Won't ever happen
		}

		return RandomStringUtils.randomAlphanumeric(6);
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.userRepository.deleteByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.userRepository.findByUserId(userId));
	}

}
