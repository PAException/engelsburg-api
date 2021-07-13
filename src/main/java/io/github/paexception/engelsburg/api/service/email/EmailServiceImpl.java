package io.github.paexception.engelsburg.api.service.email;

import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl extends LoggingComponent {

	@Autowired
	private JavaMailSender emailSender;

	public EmailServiceImpl() {
		super(EmailServiceImpl.class);
	}

	public boolean sendHtmlEmail(String subject, String recipient, String html) {
		MimeMessage mimeMessage = this.emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		try {
			helper.setText(html, true);
			helper.setTo(recipient);
			helper.setSubject(subject);
			this.emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			this.logError("An error occurred sending an email", e);
			return false;
		}

		return true;
	}

}
