package io.github.paexception.engelsburg.api.service.email;

import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service to send no-reply emails.
 */
@Component
public class EmailService implements LoggingComponent {

	final static String VERIFY_EMAIL = "<!DOCTYPE html>\n" +
			"<html lang=\"de\">\n" +
			"<head>\n" +
			"    <meta charset=\"UTF-8\">\n" +
			"    <style>\n" +
			"        * {\n" +
			"            font-family: Arial, monospace;\n" +
			"            font-size: 16px;\n" +
			"        }\n" +
			"\n" +
			"        h1 {\n" +
			"            font-size: 30px;\n" +
			"            border-bottom: solid;\n" +
			"            padding: 10px;\n" +
			"        }\n" +
			"\n" +
			"        .padding-10 {\n" +
			"            padding: 10px;\n" +
			"        }\n" +
			"\n" +
			"        .center {\n" +
			"            margin: auto;\n" +
			"            width: 50%;\n" +
			"            text-align: center;\n" +
			"        }\n" +
			"    </style>\n" +
			"    <title>Email verifizieren</title>\n" +
			"</head>\n" +
			"<body>\n" +
			"<div class=\"center padding-10\">\n" +
			"    <h1>Engelsburg-App</h1>\n" +
			"    <h3 class=\"padding-10\">Klicke hier, um deine Email zu verfizieren:</h3>\n" +
			"    <a class=\"center\" href=\"${link}\">${link}</a>\n" +
			"    <h3 class=\"padding-10\" style=\"margin-top: 40px\">oder benutze folgenden Code:</h3>\n" +
			"    <p>${code}</p>\n" +
			"</div>\n" +
			"</body>\n" +
			"</html>";
	final static String RESET_PASSWORD_EMAIL = "<!DOCTYPE html>\n" +
			"<html lang=\"de\">\n" +
			"<head>\n" +
			"    <meta charset=\"UTF-8\">\n" +
			"    <style>\n" +
			"        * {\n" +
			"            font-family: Arial, monospace;\n" +
			"            font-size: 16px;\n" +
			"        }\n" +
			"\n" +
			"        h1 {\n" +
			"            font-size: 30px;\n" +
			"            border-bottom: solid;\n" +
			"            padding: 10px;\n" +
			"        }\n" +
			"\n" +
			"        .padding-10 {\n" +
			"            padding: 10px;\n" +
			"        }\n" +
			"\n" +
			"        .center {\n" +
			"            margin: auto;\n" +
			"            width: 50%;\n" +
			"            text-align: center;\n" +
			"        }\n" +
			"    </style>\n" +
			"    <title>Passwort zurücksetzen</title>\n" +
			"</head>\n" +
			"<body>\n" +
			"<div class=\"center padding-10\">\n" +
			"    <h1>Engelsburg-App</h1>\n" +
			"    <h3 class=\"padding-10\">Klicke hier, um dein Passwort zurückzusetzen:</h3>\n" +
			"    <a class=\"center\" href=\"${link}\">${link}</a>\n" +
			"    <h3 class=\"padding-10\" style=\"margin-top: 40px\">oder benutze folgenden Code:</h3>\n" +
			"    <p>${code}</p>\n" +
			"</div>\n" +
			"</body>\n" +
			"</html>";

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
	private final EmailServiceImpl emailService;

	public EmailService(EmailServiceImpl emailService) {
		this.emailService = emailService;
	}

	/**
	 * Replace important and dynamic parts of email.
	 *
	 * @param string email
	 * @param code   to replace
	 * @param link   to replace
	 * @return modified email
	 */
	public static String replace(String string, String code, String link) {
		return string.replaceAll("(\\$\\{code})", code)
				.replaceAll("(\\$\\{link})", link.replace("${code}", code));
	}

	/**
	 * Send a request to reset the password.
	 *
	 * @param email to send the request to
	 * @param code  to reset password
	 * @return true if no error occurred
	 */
	public boolean resetPassword(String email, String code) {
		String link = Environment.PASSWORD_RESET_LINK;
		if (link == null) link = "";
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("reset-password.html");
			if (in != null) {
				return this.emailService.sendHtmlEmail("Password zurücksetzen", email,
						replace(new String(in.readAllBytes()), code, link));
			}
		} catch (IOException e) {
			this.logError("Couldn't load email preset", e, LOGGER);
		}

		return this.emailService.sendHtmlEmail(
				"Password zurücksetzen",
				email,
				replace(RESET_PASSWORD_EMAIL, code, link));
	}

	/**
	 * Send a request to verify the account.
	 *
	 * @param email to send the request to
	 * @param code  to verify account
	 * @return true if no error occurred
	 */
	public boolean verify(String email, String code) {
		String link = Environment.VERIFY_EMAIL_LINK;
		if (link == null) link = "";
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("verify-email.html");
			if (in != null) {
				return this.emailService.sendHtmlEmail("Email verifizieren", email,
						replace(new String(in.readAllBytes()), code, link));
			}
		} catch (IOException e) {
			this.logError("Couldn't load email preset", e, LOGGER);
		}

		return this.emailService.sendHtmlEmail(
				"Email verifizieren",
				email,
				replace(VERIFY_EMAIL, code, link));
	}

}
