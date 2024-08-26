package com.localreview.serviceiml;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.localreview.service.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendRegistrationEmail(String to, String subject, String name, String storeName, String qrCodeUrl) throws MessagingException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            // Đọc mẫu HTML từ thư mục
            String templatePath = "templates/emails/registration_success.html"; // Đường dẫn tới mẫu HTML
            Resource resource = new ClassPathResource(templatePath);
            String emailTemplate = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            
            // Thay thế các placeholder trong mẫu HTML
            emailTemplate = emailTemplate.replace("${name}", name)
                                         .replace("${storeName}", storeName)
                                         .replace("${qrCodeUrl}", qrCodeUrl);

            helper.setText(emailTemplate, true); // true để chỉ định nội dung HTML

            javaMailSender.send(mimeMessage);
        } catch (IOException e) {
            throw new MessagingException("Error reading email template", e);
        }
    }
}
