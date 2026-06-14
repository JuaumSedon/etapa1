package com.example.email_service.dto;

public record EmailRecordDto(
        Long userId, 
        String emailTo, 
        String subject, 
        String text
) {
}