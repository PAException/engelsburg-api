package io.github.paexception.engelsburg.api.test;

import io.github.paexception.engelsburg.api.database.model.UserModel;
import java.util.UUID;

public class TestUtils {

	public static final UUID TEST_UUID = UUID.randomUUID();
	public static final String TEST_EMAIL = "test@email.de";
	public static final String TEST_SALT = "sagsagsag";
	public static final String TEST_TOKEN = "test_token";
	public static final String TEST_PASSWORD = "password";
	public static final byte[] TEST_HASHED_PASSWORD = new byte[]{15, 35, 62, 46, 84, -96, 1, -55, 15, 118, 22, 15, 0, -71,
			-71, -71, 126, -64, 107, 11, 59, 63, -62, 26, -19, -93, -8, 15, -126, -124, -92, 81};
	public static final String TEST_NEW_PASSWORD = "new_password";
	public static final UserModel TEST_USER = new UserModel(1, TEST_UUID, TEST_EMAIL, TEST_HASHED_PASSWORD,
			TEST_SALT, false);

}
