package com.github.gabrielhumbertdev.util;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Question 1 (3.1): Email utility used to send real-time email alerts. Uses
 * Jakarta Mail (no external API calls).
 */
public class EmailUtil {

    public static void sendEmail(String to, String subject, String body) throws MessagingException {

        // SMTP configuration
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.example.com"); // placeholder
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create mail session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your-email@example.com", "your-password");
            }
        });

        // Create message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("your-email@example.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        // Send email
        Transport.send(message);
    }
}
