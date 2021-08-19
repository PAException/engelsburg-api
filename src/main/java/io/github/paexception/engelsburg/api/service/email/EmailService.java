package io.github.paexception.engelsburg.api.service.email;

import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service to send no-reply emails.
 */
@Component
public class EmailService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
	@Autowired
	private EmailServiceImpl emailService;

	/**
	 * Send a request to reset the password.
	 *
	 * @param email to send the request to
	 * @param code  to reset password
	 * @return true if no error occurred
	 */
	public boolean resetPassword(String email, String code) {
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("reset-password.html");
			if (in != null) {
				return this.emailService.sendHtmlEmail("Password zurücksetzen", email, new String(in.readAllBytes()).replace("${code}", code));
			}
		} catch (IOException e) {
			this.logError("Couldn't load email preset", e, LOGGER);
		}

		return this.emailService.sendHtmlEmail(
				"Password zurücksetzen",
				email,
				"<body>\n<h1>Code um das Passwort zurückzusetzen: ${code}</h1>\n</body>".replace("${code}", code));
	}

	/**
	 * Send a request to verify the account.
	 *
	 * @param email to send the request to
	 * @param code  to verify account
	 * @return true if no error occurred
	 */
	public boolean verify(String email, String code) {
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("verify-email.html");
			if (in != null) {
				return this.emailService.sendHtmlEmail("Email verifizieren", email, new String(in.readAllBytes()).replace("${code}", code));
			}
		} catch (IOException e) {
			this.logError("Couldn't load email preset", e, LOGGER);
		}

		return this.emailService.sendHtmlEmail(
				"Email verifizieren",
				email,
				"<body>\n<h1>Code zur Email Verifizierung: ${code}</h1>\n</body>".replace("${code}", code));
	}

}
