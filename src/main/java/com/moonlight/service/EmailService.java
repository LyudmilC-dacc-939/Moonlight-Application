package com.moonlight.service;

public interface EmailService {
    void sendEmailUponRegister(String to, String subject, String text);
}
