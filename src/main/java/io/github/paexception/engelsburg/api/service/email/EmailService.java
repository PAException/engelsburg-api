package io.github.paexception.engelsburg.api.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service to send no-reply emails.
 */
@Component
public class EmailService {

	@Autowired
	private EmailServiceImpl emailService;

	public boolean resetPassword(String email, String code) {
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("reset-password.html");
			if (in != null) {
				return this.emailService.sendHtmlEmail("Password zurücksetzen", email, new String(in.readAllBytes()).replace("${code}", code));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean verify(String email, String code) {
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("verify-email.html");
			if (in != null) {
				return this.emailService.sendHtmlEmail("Email verifizieren", email, new String(in.readAllBytes()).replace("${code}", code));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
