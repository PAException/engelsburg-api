package io.github.paexception.engelsburg.api.authentication;

import com.auth0.jwt.JWTCreator;
import io.github.paexception.engelsburg.api.EngelsburgAPI;
import io.github.paexception.engelsburg.api.authentication.dto.LoginRequestDTO;
import io.github.paexception.engelsburg.api.authentication.dto.LoginResponseDTO;
import io.github.paexception.engelsburg.api.authentication.dto.SignUpRequestDTO;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.UserRepository;
import io.github.paexception.engelsburg.api.service.EmailService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
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
			"user.data.read.self",
			"user.data.delete.self",
			"notification.settings.write.self"
	);
	private static MessageDigest md;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ScopeController scopeController;
	@Autowired
	private EmailService emailService;

	public Result<?> login(LoginRequestDTO dto) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		UserModel user = optionalUser.get();
		if (!user.getPassword().equals(this.hashPassword(dto.getPassword(), user.getSalt())))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		return Result.of(new LoginResponseDTO(this.createJWT(user)));
	}

	public Result<?> signUp(SignUpRequestDTO dto) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(dto.getEmail());
		if (optionalUser.isPresent()) return Result.of(Error.ALREADY_EXISTS, NAME_KEY);
		byte[] rawSalt = new byte[16];
		RANDOM.nextBytes(rawSalt);
		String salt = new String(rawSalt, StandardCharsets.UTF_8);
		String hashedPassword = this.hashPassword(dto.getPassword(), salt);

		UUID userId = UUID.randomUUID();
		this.userRepository.save(new UserModel(-1, userId, dto.getEmail(), hashedPassword, salt, false));
		DEFAULT_SCOPES.forEach(scope -> this.scopeController.addScope(userId, scope));//Add default scopes
		this.emailService.verify(dto.getEmail());

		return Result.empty();
	}

	public Result<?> resetPassword(String email) {
		Optional<UserModel> optionalUser = this.userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		this.emailService.resetPassword(email);

		return Result.empty();
	}

	private String createJWT(UserModel user) {
		JWTCreator.Builder builder = EngelsburgAPI.getJwtUtil()
				.createBuilder(user.getUserId().toString(), 30, Calendar.MINUTE);
		String[] scopes = this.scopeController.getScopes(user.getUserId());

		return EngelsburgAPI.getJwtUtil().sign(builder.withArrayClaim("scopes", scopes));
	}

	private String hashPassword(String password, String salt) {
		try {
			if (md == null) md = MessageDigest.getInstance("SHA-256");

			return new String(md.digest((password + salt).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException ignored) {//Won't ever happen
		}

		return null;
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.userRepository.deleteByUserId(userId);
	}

	@Override
	public Result<?> getUserData(UUID userId) {
		return Result.of(this.userRepository.findByUserId(userId));
	}

}
