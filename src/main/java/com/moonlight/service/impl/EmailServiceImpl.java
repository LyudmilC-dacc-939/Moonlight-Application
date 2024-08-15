package com.moonlight.service.impl;

import com.moonlight.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String fromEmail;

    public void sendEmailUponRegister(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to); // Recipient Email
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            System.out.println("Error sending registration email: " + ex.getMessage());
        }
    }

    @Override
    public void sendEmailForForgottenPassword(String to, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Moonlight - Reset Password");
        message.setText("You're new password is: " + password + "\nWe recommend, for security purpose, " +
                "to update your password once you log in" + "\n\nBest regards, \nYour service Team");
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            System.out.println("Error sending registration email: " + ex.getMessage());
        }
    }
}
