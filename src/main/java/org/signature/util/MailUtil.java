package org.signature.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class MailUtil {

    private static final Logger LOGGER = LogManager.getLogger(MailUtil.class);

    private static final String sender_receiver = "******************@gmail.com"; //Enter your gmail ID to be used as mail server

    public static boolean sendMail(String user, String content) {
        try {
            URL url = new URL("https://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();

            new Thread(() -> {
                String password = "*************"; // Enter you gmail password
                String host = "smtp.gmail.com";

                String subject = "Feedback from " + user;

                final Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", "587");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender_receiver, password);
                    }
                });

                Message message = prepareMessage(session, subject, content);
                if (message != null) {
                    try {
                        Transport.send(message);
                    } catch (MessagingException ex) {
                        LOGGER.log(Level.ERROR, "Submission failed! ", ex);
                    }
                } else {
                    LOGGER.log(Level.INFO, "Message is not available, so cannot send!");
                }
            }).start();

            return true;
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "Failed to connect to mail server! ", ex);
            return false;
        }
    }

    private static Message prepareMessage(Session session, String subject, String content) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender_receiver));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(sender_receiver));
            message.setSubject(subject);
            message.setContent(content, "text/html");
            return message;
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Failed to prepare message! ", ex);
        }
        return null;
    }
}
