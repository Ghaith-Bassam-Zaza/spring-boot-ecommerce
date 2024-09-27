package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.api.exceptions.EmailFailureException;
import com.backend.ecommerce_backend.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${email.from}")
    private String from;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public SimpleMailMessage createMailMassage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        return message;
    }
    public void sendVerificationMail(VerificationToken token) throws EmailFailureException {
        SimpleMailMessage message = createMailMassage();
        message.setTo(token.getLocalUser().getEmail());
        message.setSubject("Account Verification.");
        message.setText("Please follow the provided link to activate your account.\n" + frontendUrl + "/auth/verify?token=" + token.getToken() );
        try{
            mailSender.send(message);
        }catch (MailException e) {
            throw new EmailFailureException();
        }
    }
}
