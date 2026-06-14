package com.example.email_service.service;

import com.example.email_service.dto.EmailRecordDto;
import com.example.email_service.enums.StatusEmail;
import com.example.email_service.model.EmailModel;
import com.example.email_service.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public void sendEmail(EmailRecordDto emailDto) {
        EmailModel emailModel = new EmailModel();
        emailModel.setUserId(emailDto.userId());
        emailModel.setEmailTo(emailDto.emailTo());
        emailModel.setSubject(emailDto.subject());
        emailModel.setText(emailDto.text());
        emailModel.setEmailFrom(emailFrom);
        emailModel.setSendDateEmail(LocalDateTime.now());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailModel.getEmailFrom());
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getSubject());
            message.setText(emailModel.getText());
            
            emailSender.send(message); 
            
            emailModel.setStatus(StatusEmail.SENT);
        } catch (MailException e) {
            emailModel.setStatus(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel); 
        }
    }
}