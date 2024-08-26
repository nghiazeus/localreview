package com.localreview.service;

import javax.mail.MessagingException;

public interface EmailService {
    void sendRegistrationEmail(String to, String subject, String name, String storeName, String qrCodeUrl) throws MessagingException;
}