package com.project.SuperC.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications to users.
 */
@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a simple email notification to a specified recipient.
     *
     * @param toEmail The recipient's email address.
     * @param subject The subject line of the email.
     * @param price The price to be included in the email body.
     */
    public void emailNotificationSender(String toEmail,
                                        String subject,
                                        float price){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("voleg239@gmail.com");
        message.setTo(toEmail);
        message.setText("The price for " + subject + " is $" + price + " today at SuperC.");
        message.setSubject(subject + " is $" + price);


        mailSender.send(message);

        System.out.println("Emails sent to: " + toEmail + " for " + subject);
    }
}