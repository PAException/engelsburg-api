package io.github.paexception.engelsburg.api.service.email;

import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
	private final JavaMailSender emailSender;

	public EmailServiceImpl(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * Send a html email.
	 *
	 * @param subject   of email
	 * @param recipient of email
	 * @param html      to send
	 * @return true if no error occurred
	 */
	public boolean sendHtmlEmail(String subject, String recipient, String html) {
		MimeMessage mimeMessage = this.emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		try {
			helper.setText(html, true);
			helper.setTo(recipient);
			helper.setSubject(subject);
			this.emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			this.logError("An error occurred sending an email", e, LOGGER);
			return false;
		}

		return true;
	}

}
