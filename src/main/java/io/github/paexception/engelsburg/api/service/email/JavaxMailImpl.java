package io.github.paexception.engelsburg.api.service.email;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

@Component
public class JavaxMailImpl {

	private static final String SMTP_SERVER = System.getenv("SMTP_SERVER");
	private static final String SMTP_PORT = System.getenv("SMTP_PORT");
	private static final String USERNAME = System.getenv("EMAIL_USERNAME");
	private static final String PASSWORD = System.getenv("EMAIL_PASSWORD");
	private static Session session;

	@PostConstruct
	public static void init() {
		Properties properties = System.getProperties();

		properties.put("mail.smtp.host", SMTP_SERVER);
		properties.put("mail.smtp.port", SMTP_PORT);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});
	}

	public static boolean sendHtmlEmail(String subject, String recipient, String html) {
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(USERNAME));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
			msg.setSubject(subject);
			msg.setDataHandler(new DataHandler(new HTMLDataSource(html)));

			Transport.send(msg);
		} catch (MessagingException ignored) {
			ignored.printStackTrace();
			return false;
		}
		return true;
	}

	private static void sendMimeMessage(Message msg) throws MessagingException {
		SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
		t.connect(SMTP_SERVER, USERNAME, PASSWORD);
		t.sendMessage(msg, msg.getAllRecipients());
		System.out.println("Response: " + t.getLastServerResponse());
		t.close();
	}

	@EventListener(ApplicationStartedEvent.class)
	public void test() {
		sendHtmlEmail("Test", "huerkamp.paul@gmail.com", "<h1>TEST VON PAUL</h1>");
	}

	private static class HTMLDataSource implements DataSource {

		private final String html;

		public HTMLDataSource(String htmlString) {
			this.html = htmlString;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			if (this.html == null) throw new IOException("html message is null!");
			return new ByteArrayInputStream(this.html.getBytes());
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("This DataHandler cannot write HTML");
		}

		@Override
		public String getContentType() {
			return "text/html";
		}

		@Override
		public String getName() {
			return "HTMLDataSource";
		}

	}


}
