package com.jrquiz.service.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.jrquiz.entity.User;
import com.jrquiz.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	private String username = "javarushmailrobot";
	private String password = "NthhfGf$$d5hl";
	private String host = "http://localhost:8080/com.jrquiz/"; // "http://93.183.222.82:4345/JRQuiz/";
	private Properties props;

	@Override
	public boolean sendRegistrationEmail(User user) {
		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			// от кого
			message.setFrom(new InternetAddress(username));
			// кому
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			// Заголовок письма
			message.setSubject("registration");
			// Содержимое
			message.setText("To confirm registration you need go to " + host + "confirm?id=" + user.getEmailtoken() + "  . Thanks!");

			// Отправляем сообщение
			Transport.send(message);
		} catch (MessagingException e) {
			// throw new RuntimeException(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean sendRemaindPasswordMail(User user) {
		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			// от кого
			message.setFrom(new InternetAddress(username));
			// кому
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			// Заголовок письма
			message.setSubject("Remind password");
			// Содержимое
			message.setText("To chacge password you need go to " + host + "remind?id=" + user.getEmailtoken() + "  . Thanks!");

			// Отправляем сообщение
			Transport.send(message);
		} catch (MessagingException e) {
			// throw new RuntimeException(e);
			return false;
		}
		return true;
	}

}
