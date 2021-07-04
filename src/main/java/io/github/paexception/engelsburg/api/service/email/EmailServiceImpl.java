package io.github.paexception.engelsburg.api.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl {

	@Autowired
	private JavaMailSender emailSender;

	public boolean sendHtmlEmail(String subject, String recipient, String html) {
		MimeMessage mimeMessage = this.emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		try {
			helper.setText(html, true);
			helper.setTo(recipient);
			helper.setSubject(subject);
			this.emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
