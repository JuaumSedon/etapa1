package com.example.email_service.consumer;

import com.example.email_service.dto.EmailRecordDto;
import com.example.email_service.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailRecordDto emailRecordDto) {
        System.out.println("📦 Nova mensagem recebida na fila! Enviando e-mail para: " + emailRecordDto.emailTo());
        emailService.sendEmail(emailRecordDto);
    }
}