package com.project.SuperC.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications.
 */
@Service
@AllArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendNotificationsEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your_sender_email@example.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to {} with subject: {}", toEmail, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {} with subject {}: {}", toEmail, subject, e.getMessage(), e);
        }
    }
}