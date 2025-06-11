package com.project.SuperC.service;

// org.apache.logging.log4j.message.SimpleMessage is not used, can be removed.
// import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications to users.
 * This class abstracts the email sending logic and uses Spring's JavaMailSender.
 */
@Service // Marks this class as a Spring service component.
public class EmailSenderService {

    /**
     * Spring's MailSender interface, automatically injected by Spring.
     * Used to send email messages.
     */
    @Autowired // Injects the JavaMailSender bean configured in application.properties.
    private JavaMailSender mailSender;

    /**
     * Sends a simple email notification to a specified recipient.
     *
     * @param toEmail The recipient's email address.
     * @param subject The subject line of the email.
     * @param price The price to be included in the email body (e.g., current product price).
     */
    public void emailNotificationSender(String toEmail,
                                        String subject,
                                        float price){
        SimpleMailMessage message = new SimpleMailMessage();
        // Set the sender's email address. This should typically match spring.mail.username.
        message.setFrom("voleg239@gmail.com");
        message.setTo(toEmail);
        message.setText("The price for " + subject + " is $" + price + " today at SuperC.");
        message.setSubject(subject + " is $" + price);

        // Send the constructed email message.
        mailSender.send(message);

        System.out.println("Emails sent to: " + toEmail + " for " + subject); // Log more specifically
    }
}